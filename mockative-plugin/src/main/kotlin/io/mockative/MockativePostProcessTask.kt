package io.mockative

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.name

open class MockativePostProcessTask : DefaultTask() {
    init {
        outputs.upToDateWhen { false }
    }

    @TaskAction
    fun run() {
        return
        println("[MockativePostProcessTask] run")

        val testTasks = project.testTasks
        if (testTasks.isEmpty()) {
            println("[MockativePostProcessTask] no test tasks detected")
            return
        }

        val kspDirectory = project.layout.buildDirectory.dir("generated/ksp").get()
        println("[MockativePostProcessTask] kspDirectory: $kspDirectory")

        // Move `*Mock.kt` files
        val mockFiles = kspDirectory.asFileTree.matching { it.include("**/*Mock.kt").include("**/*.Mockative.kt") }

        val mockativeDir = project.mockativeDir

        val kspDirectoryFile = kspDirectory.asFile
        for (sourceFile in mockFiles) {
            val destPath = sourceFile.relativeTo(kspDirectoryFile).toPath()
                .drop(1)
                .mapIndexed { index, path -> if (index == 0) Path(path.name.replace("Main", "Test")) else path }
                .joinToString(File.separator)

            val destFile = File(mockativeDir, destPath)
            println("[MockativePostProcessTask] $sourceFile -> $destFile")

            destFile.parentFile.mkdirs()
            sourceFile.renameTo(destFile)
        }
    }
}
