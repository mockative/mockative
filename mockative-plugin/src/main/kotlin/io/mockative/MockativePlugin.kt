package io.mockative

import com.google.devtools.ksp.gradle.KspAATask
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import java.io.File

abstract class MockativePlugin : Plugin<Project> {
    private val version = "3.0.1-SNAPSHOT"

    override fun apply(project: Project) {
        // Log project properties for diagnostics
        logProjectProperties(project)

        // Configure sourceSets
        project.kotlinExtension.sourceSets.configureEach { sourceSet ->
            val file = File(project.mockativeDir, "${sourceSet.name}/kotlin")
            sourceSet.kotlin.srcDir(file)
        }

        // Prepare extension
        val mockative = project.extensions.create("mockative", MockativeExtension::class.java)

        when {
            project.isMockativeDisabled -> {
                project.info("Disabling Mockative as a result of the Gradle property 'io.mockative.enabled=false'")
            }
            project.isMockativeEnabled -> {
                applyPlugin(project, mockative, "the Gradle property 'io.mockative.enabled=true'")
            }
            project.isRunningVerificationTask -> {
                applyPlugin(project, mockative, "'Verification' task detected")
            }
            project.isRunningTestPrefix -> {
                applyPlugin(project, mockative, "task with 'test' prefix detected")
            }
            project.isRunningTestSuffix -> {
                applyPlugin(project, mockative, "task with 'Test' suffix detected")
            }
            project.isRunningTestsSuffix -> {
                applyPlugin(project, mockative, "task with 'Tests' suffix detected")
            }
        }
    }

    private fun logProjectProperties(project: Project) {
        project.debug("io.mockative.enabled=${project.findProperty("io.mockative.enabled")}")
        project.debug("gradle.startParameter.taskNames=${project.gradle.startParameter.taskNames.joinToString()}")
        project.debug("gradle.taskGraph.allTasks=${project.gradle.taskGraph.allTasks.toDescription()}")

        project.debug("mockativeDir: ${project.mockativeDir}")
        project.debug("isMockativeEnabled: ${project.isMockativeEnabled}")
        project.debug("isMockativeDisabled: ${project.isMockativeDisabled}")
        project.debug("isRunningVerificationTask: ${project.isRunningVerificationTask}")
        project.debug("isRunningTestPrefix: ${project.isRunningTestPrefix}")
        project.debug("isRunningTestSuffix: ${project.isRunningTestSuffix}")
        project.debug("isRunningTestsSuffix: ${project.isRunningTestsSuffix}")
    }

    private fun applyPlugin(project: Project, mockative: MockativeExtension, reason: String) {
        project.info("Enabling Mockative as a result of $reason")

        project.pluginManager.apply("com.google.devtools.ksp")
        project.pluginManager.apply("org.jetbrains.kotlin.plugin.allopen")

        // Configure the all-open plugin
        project.extensions.configure(AllOpenExtension::class.java) { allOpen ->
            allOpen.annotation("io.mockative.Mockable")
        }

        // Prepare task to configure mockative
        val mockativeConfiguration =
            project.tasks.register("mockativeConfiguration", MockativeConfigurationTask::class.java)
        val mockativeProcessRuntime =
            project.tasks.register("mockativeProcessRuntime", MockativeProcessRuntimeTask::class.java)

        // Assign mockative configuration task as dependency of KSP tasks
        project.tasks.withType(KspAATask::class.java) { ksp ->
            ksp.dependsOn(mockativeConfiguration)
            ksp.dependsOn(mockativeProcessRuntime)
        }

        // Add `mockative-processor` as dependency of KSP configurations
        project.configurations.whenObjectAdded { configuration ->
            if (configuration.name != "ksp" && configuration.name.startsWith("ksp")) {
                project.dependencies.add(configuration.name, "io.mockative:mockative-processor:$version")
            }
        }

        // Add `mockative` as a dependency of `commonMain`
        val sourceSets = project.kotlinExtension.sourceSets

        if (project.isMultiplatform) {
            sourceSets.getByName("commonMain") {
                it.dependencies {
                    implementation("io.mockative:mockative:$version")
                }
            }
        } else {
            sourceSets.getByName("main") {
                it.dependencies {
                    implementation("io.mockative:mockative:$version")
                }
            }
        }

        // Pass extension configuration to symbol processor through KSP `arg`s
        project.afterEvaluate {
            project.extensions.configure(KspExtension::class.java) { ksp ->
                val excludeMembers = mockative.excludeMembers.get().joinToString(",")
                ksp.arg("io.mockative:mockative:exclude-members", excludeMembers)

                val excludeKotlinDefaultMembers = mockative.excludeKotlinDefaultMembers.get().toString()
                ksp.arg("io.mockative:mockative:exclude-kotlin-default-members", excludeKotlinDefaultMembers)

                val stubsUnitByDefault = mockative.stubsUnitByDefault.get()
                ksp.arg("io.mockative:mockative:stubsUnitByDefault", "$stubsUnitByDefault")
            }

            // Modifying dependencies for Android targets at task action time is prohibited, so we use this deduction
            // during configuration time to do a "best effort" of adding JVM dependencies for Android targets as needed.
            project.addJVMDependencies("jvmMain")
            project.addJVMDependencies("androidMain")
        }
    }
}
