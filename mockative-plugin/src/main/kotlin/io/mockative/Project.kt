package io.mockative

import org.gradle.api.Project
import org.gradle.api.tasks.testing.AbstractTestTask
import java.io.File

internal val Project.mockativeConfigurationFile: File
    get() = File(layout.buildDirectory.get().asFile, "mockative.conf")

internal val Project.testTasks: List<AbstractTestTask>
    get() = gradle.taskGraph.allTasks.filterIsInstance<AbstractTestTask>()
