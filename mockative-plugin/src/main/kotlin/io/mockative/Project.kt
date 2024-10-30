package io.mockative

import com.android.build.gradle.internal.tasks.AndroidTestTask
import org.gradle.api.Project
import org.gradle.api.tasks.testing.AbstractTestTask
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import java.io.File

internal fun Project.runMockative(block: () -> Unit) {
    when {
        isMockativeEnabled -> {
            info("Plugin enabled by presence of the Gradle property 'io.mockative.enabled=true'")
            return block()
        }
        isMockativeDisabled -> {
            info("Plugin disabled by presence of the Gradle property 'io.mockative.enabled=false'")
        }
        testTasks.isNotEmpty() -> {
            info("Plugin enabled by detected test tasks: ${testTasks.joinToString(", ") { "'$it'" }}")
            return block()
        }
        deviceTestTasks.isNotEmpty() -> {
            warn("Using Mockative with Android connected tests requires setting the Gradle property 'io.mockative.enabled=true' when launching your Gradle task.")
        }
        else -> {
            info("Plugin disabled by lack of enabling condition")
        }
    }
}

internal fun Project.info(message: Any?) {
    logger.info("[MockativePlugin] $message")
}

internal fun Project.warn(message: Any?) {
    logger.warn("[MockativePlugin] $message")
}

internal fun Project.debug(message: Any?) {
    logger.debug("[MockativePlugin] $message")
}

private val Project.testTasks: List<AbstractTestTask>
    get() = gradle.taskGraph.allTasks.filterIsInstance<AbstractTestTask>()

private val Project.deviceTestTasks: List<AndroidTestTask>
    get() = gradle.taskGraph.allTasks.filterIsInstance<AndroidTestTask>()

internal val Project.mockativeDir: File
    get() = layout.buildDirectory.dir("generated/mockative").get().asFile

internal val Project.isMockativeDisabled: Boolean
    get() = findProperty("io.mockative.enabled") == "false"

internal val Project.isMockativeEnabled: Boolean
    get() = findProperty("io.mockative.enabled") == "true"

internal fun Project.addJVMDependencies(sourceSetName: String) {
    val sourceSet = kotlinExtension.sourceSets.firstOrNull { it.name == sourceSetName } ?: return

    info("Adding JVM runtime dependencies to source set '${sourceSet.name}'")

    sourceSet.dependencies {
        implementation(kotlin("reflect"))
        implementation("org.objenesis:objenesis:3.3")
        implementation("org.javassist:javassist:3.29.2-GA")
    }
}
