package io.mockative

import com.android.build.gradle.internal.tasks.AndroidTestTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.VerificationTask
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import java.io.File

internal fun Project.runMockative(block: () -> Unit) {
    when {
        isMockativeEnabled -> {
            info("Plugin enabled by presence of the Gradle property 'io.mockative.enabled=true'")
            block()
        }
        isMockativeDisabled -> {
            info("Plugin disabled by presence of the Gradle property 'io.mockative.enabled=false'")
        }
        deviceTestTasks.isNotEmpty() -> {
            warn("Using Mockative with Android connected tests requires setting the Gradle property 'io.mockative.enabled=true' when launching your Gradle task.")
        }
        verificationTasks.isNotEmpty() -> {
            info("Plugin enabled by detected verification tasks: ${verificationTasks.joinToString(", ") { "'${it.name}'" }}")
            block()
        }
        isRunningConnectedAndroidTests -> {
            info("Plugin enabled by detected connected Android tests")
            block()
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

private val Project.verificationTasks: List<Task>
    get() = gradle.taskGraph.allTasks.filter { it is VerificationTask }

private val Project.deviceTestTasks: List<AndroidTestTask>
    get() = gradle.taskGraph.allTasks.filterIsInstance<AndroidTestTask>()

internal val Project.mockativeDir: File
    get() = layout.buildDirectory.dir("generated/mockative").get().asFile

internal val Project.isMockativeDisabled: Boolean
    get() = findProperty("io.mockative.enabled") == "false"

internal val Project.isMockativeEnabled: Boolean
    get() = findProperty("io.mockative.enabled") == "true"

internal val Project.isRunningConnectedAndroidTests: Boolean
    get() = gradle.startParameter.taskNames.any { it.startsWith("connected") && it.endsWith("AndroidTest") }

internal fun Project.addJVMDependencies(sourceSetName: String) {
    val sourceSet = kotlinExtension.sourceSets.firstOrNull { it.name == sourceSetName } ?: return

    info("Adding JVM runtime dependencies to source set '${sourceSet.name}'")

    sourceSet.dependencies {
        implementation(kotlin("reflect"))
        implementation("org.objenesis:objenesis:3.3")
        implementation("org.javassist:javassist:3.29.2-GA")
    }
}
