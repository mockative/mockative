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

        project.runMockative {
            project.debug("Copying runtime from resources")

            val resources = ResourceManager(project, {}.javaClass)

            val dst = mockativeDir.toPath()
            project.debug("Copying resources to '$dst'")
            resources.copyRecursively("/src", dst)

            project.addJVMDependencies("jvmMain")

            if (!project.isMockativeEnabled) {
                // Replace android implementation with stub
                project.info("Replacing android implementation with stub")
                resources.copyRecursively("/src/androidStubMain", dst.resolve("androidMain"))
            }
        }
    }
}
