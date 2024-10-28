package io.mockative

import com.google.devtools.ksp.gradle.KspAATask
import com.google.devtools.ksp.gradle.KspExtension
import com.google.devtools.ksp.gradle.KspTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.extensions.stdlib.capitalized
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import java.io.File

abstract class MockativePlugin : Plugin<Project> {
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
//            if (sourceSet.name.endsWith("Test")) {
//                val mainSrcSet = sourceSet.name.removeSuffix("Test") + "Main"
//                println("[MockativePlugin] Adding source directory '$mainSrcSet/kotlin' for source set '${sourceSet.name}'")
//                val file = File(project.mockativeDir, "${mainSrcSet}/kotlin")
//                sourceSet.kotlin.srcDir(file)
//            }
            val file = File(project.mockativeDir, "${sourceSet.name}/kotlin")
            sourceSet.kotlin.srcDir(file)
        }

        // Prepare task to configure mockative
        val mockativeConfiguration = project.tasks.register("mockativeConfiguration", MockativeConfigurationTask::class.java)
        val mockativeProcessRuntime = project.tasks.register("mockativeProcessRuntime", MockativeProcessRuntimeTask::class.java)
//        val mockativePostProcess = project.tasks.register("mockativePostProcess", MockativePostProcessTask::class.java)

        // Assign mockative configuration task as dependency of KSP tasks
        project.tasks.withType(KspAATask::class.java) { ksp ->
            ksp.dependsOn(mockativeConfiguration)
            ksp.dependsOn(mockativeProcessRuntime)

//            val taskName = "mockativePostProcess${ksp.name.capitalized()}"
//
//            // TODO Optimize by passing argument to constructor
//            val mockativePostProcess = project.tasks.register(taskName, MockativePostProcessTask::class.java)
//
//            ksp.finalizedBy(mockativePostProcess)
        }

//        project.tasks.withType(KotlinCompilationTask::class.java) { task ->
//            val taskName = "mockativePostProcessKsp${task.name.removePrefix("compile")}"
//            task.mustRunAfter(taskName)
//        }

        // Add `mockative-processor` as dependency of KSP configurations
        project.configurations.whenObjectAdded { configuration ->
            if (configuration.name != "ksp" && configuration.name.startsWith("ksp")) {
                val dependency = project.dependencies.project(mapOf("path" to ":mockative-processor"))
                project.dependencies.add(configuration.name, dependency)
            }
        }

        // Pass extension configuration to symbol processor through KSP `arg`s
        project.afterEvaluate {
            project.extensions.configure(KspExtension::class.java) { ksp ->
                mockative.optIn.get().forEach { (pckg, annotations) ->
                    if (pckg == "*") {
                        ksp.arg("io.mockative:mockative:opt-in", annotations.joinToString(","))
                    } else {
                        ksp.arg("io.mockative:mockative:opt-in:$pckg", annotations.joinToString(","))
                    }
                }

                val excludeMembers = mockative.excludeMembers.get().joinToString(",")
                ksp.arg("io.mockative:mockative:exclude-members", excludeMembers)

                val excludeKotlinDefaultMembers = mockative.excludeKotlinDefaultMembers.get().toString()
                ksp.arg("io.mockative:mockative:exclude-kotlin-default-members", excludeKotlinDefaultMembers)

                val stubsUnitByDefault = mockative.stubsUnitByDefault.get()
                ksp.arg("io.mockative:mockative:stubsUnitByDefault", "$stubsUnitByDefault")
            }
        }
    }
}
