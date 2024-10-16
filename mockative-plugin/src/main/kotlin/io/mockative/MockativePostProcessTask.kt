package io.mockative

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class MockativePostProcessTask : DefaultTask() {
    @TaskAction
    fun run() {
        val testTasks = project.testTasks
        println("[MockativePostProcessTask] testTasks: $testTasks")

        testTasks.forEach { testTask ->
            val sharedTargetDir = testTask.name.replace("Test", "")
            val testTargetDir = "${sharedTargetDir}Test"
            val mainTargetDir = "${sharedTargetDir}Main"

            val mainGeneratedDir = project.layout.buildDirectory.get()
                .dir("generated/ksp/$sharedTargetDir/$mainTargetDir/kotlin")
                .asFile

//            inputs.dir(mainGeneratedDir)

            println("[MockativePostProcessTask] mainGeneratedDir: $mainGeneratedDir")

            val testGeneratedDir = project.layout.buildDirectory.get()
                .dir("generated/ksp/$sharedTargetDir/$testTargetDir/kotlin")
                .asFile

//            outputs.dir(mainGeneratedDir)

            println("[MockativePostProcessTask] testGeneratedDir: $testGeneratedDir")

            mainGeneratedDir.walk()
                .filter { it.isFile }
                .forEach { mainGeneratedFile ->
                    val relativePath = mainGeneratedFile.toRelativeString(mainGeneratedDir)

                    val testGeneratedFile = File(testGeneratedDir, relativePath)
                    println("Copying '${mainGeneratedFile}' to '${testGeneratedFile}'")

                    mainGeneratedFile.copyTo(testGeneratedFile, overwrite = true)

                    mainGeneratedFile.delete()
                }
        }
    }
}
