package io.mockative

import com.android.build.gradle.internal.tasks.AndroidTestTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.VerificationTask
import org.gradle.api.tasks.testing.AbstractTestTask
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import java.io.File
import kotlin.reflect.jvm.jvmName

internal fun Project.runMockative(block: () -> Unit) {
    when {
        isMockativeEnabled -> {
            info("Plugin enabled by presence of the Gradle property 'io.mockative.enabled=true'")
            block()
        }
        isMockativeDisabled -> {
            info("Plugin disabled by presence of the Gradle property 'io.mockative.enabled=false'")
        }
        verificationTasks.isNotEmpty() -> {
            info("Plugin enabled by detected verification tasks: ${verificationTasks.joinToString(", ") { "'${it.name}'" }}")
            block()
        }
        isRunningTestPrefix -> {
            info("Plugin enabled by detected 'test' prefix task")
            block()
        }
        isRunningTestSuffix -> {
            info("Plugin enabled by detected 'Test' suffix task")
            block()
        }
        isRunningTestsSuffix -> {
            info("Plugin enabled by detected 'Tests' suffix task")
            block()
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
    logger.warn("[MockativePlugin] $message")
}

internal fun Project.warn(message: Any?) {
    logger.warn("[MockativePlugin] $message")
}

internal fun Project.debug(message: Any?) {
    logger.warn("[MockativePlugin] $message")
}

internal fun Task.toDescription(): String {
    return "$name <${this::class.jvmName}>"
}

internal fun Iterable<Task>.toDescription(): String {
    return "[${joinToString { it.toDescription() }}]"
}

internal val Project.verificationTasks: List<Task>
    get() = gradle.taskGraph.allTasks.filter { it is VerificationTask }

internal val Project.testTasks: List<AbstractTestTask>
    get() = gradle.taskGraph.allTasks.filterIsInstance<AbstractTestTask>()

internal val Project.deviceTestTasks: List<Task>
    get() = gradle.taskGraph.allTasks.filter { it is AndroidTestTask }

internal val Project.mockativeDir: File
    get() = layout.buildDirectory.dir("generated/mockative").get().asFile

internal val Project.isMockativeDisabled: Boolean
    get() = findProperty("io.mockative.enabled") == "false"

internal val Project.isMockativeEnabled: Boolean
    get() = findProperty("io.mockative.enabled") == "true"

internal val Project.isRunningTestPrefix: Boolean
    get() = gradle.startParameter.taskNames.any { it.startsWith("test") }

internal val Project.isRunningTestSuffix: Boolean
    get() = gradle.startParameter.taskNames.any { it.endsWith("Test") }

internal val Project.isRunningTestsSuffix: Boolean
    get() = gradle.startParameter.taskNames.any { it.endsWith("Tests") }

/**
 * This check enables linters that perform Kotlin compilation like Detekt, by replacing the Android
 * implementation of `mock` with a stub, since the Android Gradle Plugin prohibits modifying Android
 * dependencies during a task action.
 */
internal val Project.isRunningCompilingLinter: Boolean
    get() = !project.isMockativeEnabled && !project.isRunningTestSuffix && project.testTasks.isEmpty()

internal val Project.isMultiplatform: Boolean
    get() = project.kotlinExtension is KotlinMultiplatformExtension

internal fun Project.addJVMDependencies(sourceSetName: String, reason: String? = null) {
    val sourceSet = kotlinExtension.sourceSets.firstOrNull { it.name == sourceSetName } ?: return

    info("Adding JVM runtime dependencies to source set '${sourceSet.name}'${reason?.let { " as a result of $it" } ?: ""}")

    sourceSet.dependencies {
        implementation(kotlin("reflect"))
        implementation("org.objenesis:objenesis:3.3")
        implementation("org.javassist:javassist:3.29.2-GA")
    }
}
