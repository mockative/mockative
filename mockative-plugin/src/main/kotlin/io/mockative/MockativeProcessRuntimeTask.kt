package io.mockative

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class MockativeProcessRuntimeTask : DefaultTask() {
    init {
        outputs.upToDateWhen { false }
    }

    @TaskAction
    fun run() {
        val mockativeDir = project.mockativeDir

        project.debug("Deleting runtime from '$mockativeDir'")
        mockativeDir.deleteRecursively()

        project.debug("Copying runtime from resources")
        val resources = ResourceManager(project, {}.javaClass)

        val dst = mockativeDir.toPath()
        project.debug("Copying resources to '$dst'")
        resources.copyRecursively("/src", dst)

        // This check enables linters that perform Kotlin compilation like Detekt, by replacing the Android
        // implementation of `mock` with a stub, since the Android Gradle Plugin prohibits modifying Android
        // dependencies during a task action.
        if (!project.isMockativeEnabled && !project.isRunningTestSuffix && project.testTasks.isEmpty()) {
            project.info("Replacing android implementation with stub because a linter is detected")
            resources.copyRecursively("/src/androidStubMain", dst.resolve("androidMain"))
        }
    }
}
