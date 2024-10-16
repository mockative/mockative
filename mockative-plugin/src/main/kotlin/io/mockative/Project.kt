package io.mockative

import org.gradle.api.Project
import org.gradle.api.tasks.testing.AbstractTestTask
import java.io.File

internal val Project.testTasks: List<AbstractTestTask>
    get() = gradle.taskGraph.allTasks.filterIsInstance<AbstractTestTask>()

internal val Project.mockativeDir: File
    get() = layout.buildDirectory.dir("generated/mockative").get().asFile
