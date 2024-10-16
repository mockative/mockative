package io.mockative

import com.google.devtools.ksp.gradle.KspAATask
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import java.io.File

abstract class MockativePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply("com.google.devtools.ksp")
        project.pluginManager.apply("org.jetbrains.kotlin.plugin.allopen")

        // Configure sourceSets
        project.kotlinExtension.sourceSets.configureEach { sourceSet ->
            val file = File(project.mockativeDir, "${sourceSet.name}/kotlin")
            sourceSet.kotlin.srcDir(file)
        }

        // Prepare extension
        val mockative = project.extensions.create("mockative", MockativeExtension::class.java)

        // Prepare task to configure mockative
        val mockativeConfigure = project.tasks.register("mockativeConfigure", ConfigureMockativeTask::class.java)

        val mockativeCopyRuntime = project.tasks.register("mockativeCopyRuntime", MockativeCopyRuntimeTask::class.java)

        // Assign mockative configuration task as dependency of KSP tasks
        project.tasks.withType(KspAATask::class.java) { ksp ->
            ksp.dependsOn(mockativeConfigure)
            ksp.dependsOn(mockativeCopyRuntime)

//            ksp.inputs.file(project.mockativeConfigurationFile)
        }

        // Add `mockative-processor` as dependency of KSP configurations
        project.configurations.whenObjectAdded { configuration ->
            if (configuration.name != "ksp" && configuration.name.startsWith("ksp")) {
                val dependency = project.dependencies.project(mapOf("path" to ":mockative-processor"))
                project.dependencies.add(configuration.name, dependency)
            }
        }

        project.kotlinExtension.sourceSets.configureEach { sourceSet ->
            println("[MockativePlugin] sourceSet: ${sourceSet.name}")
            sourceSet.kotlin.srcDir(project.mockativeConfigurationFile)
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

                val stubsUnitByDefault = mockative.stubsUnitByDefault.get()
                ksp.arg("mockative.stubsUnitByDefault", "$stubsUnitByDefault")
            }
        }
    }
}
