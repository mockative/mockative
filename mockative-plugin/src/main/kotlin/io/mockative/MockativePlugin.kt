package io.mockative

import com.google.devtools.ksp.gradle.KspAATask
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import java.io.File

abstract class MockativePlugin : Plugin<Project> {
    private val version = "3.0.0"

    override fun apply(project: Project) {
        project.pluginManager.apply("com.google.devtools.ksp")
        project.pluginManager.apply("org.jetbrains.kotlin.plugin.allopen")

        // Prepare extension
        val mockative = project.extensions.create("mockative", MockativeExtension::class.java)

        // Exit if mockative is explicitly disabled
        if (project.isMockativeDisabled) {
            return
        }

        // Configure the all-open plugin
        project.extensions.configure(AllOpenExtension::class.java) { allOpen ->
            allOpen.annotation("io.mockative.Mockable")
        }

        // Configure sourceSets
        project.kotlinExtension.sourceSets.configureEach { sourceSet ->
            val file = File(project.mockativeDir, "${sourceSet.name}/kotlin")
            sourceSet.kotlin.srcDir(file)
        }

        // Prepare task to configure mockative
        val mockativeConfiguration = project.tasks.register("mockativeConfiguration", MockativeConfigurationTask::class.java)
        val mockativeProcessRuntime = project.tasks.register("mockativeProcessRuntime", MockativeProcessRuntimeTask::class.java)

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
        project.kotlinExtension.sourceSets.getByName("commonMain") { sourceSet ->
            sourceSet.dependencies {
                implementation("io.mockative:mockative:$version")
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

            // Add JVM dependencies to Android targets during test
            if (project.isMockativeEnabled) {
                project.addJVMDependencies("androidMain")
            }
        }
    }
}
