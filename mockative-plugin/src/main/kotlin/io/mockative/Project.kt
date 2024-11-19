package io.mockative

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.VerificationTask
import org.gradle.api.tasks.testing.AbstractTestTask
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import java.io.File
import kotlin.reflect.jvm.jvmName

internal fun Project.info(message: Any?) {
    println("[Mockative] $message")
}

internal fun Project.warn(message: Any?) {
    println("[Mockative] $message")
}

internal fun Project.debug(message: Any?) {
    println("[Mockative] $message")
}

internal fun Task.toDescription(): String {
    return "$name <${this::class.jvmName}>"
}

internal fun Iterable<Task>.toDescription(): String {
    return joinToString { it.toDescription() }
}

internal val Project.testTasks: List<AbstractTestTask>
    get() = gradle.taskGraph.allTasks.filterIsInstance<AbstractTestTask>()

internal val Project.mockativeDir: File
    get() = layout.buildDirectory.dir("generated/mockative").get().asFile

internal val Project.isMockativeDisabled: Boolean
    get() = findProperty("io.mockative.enabled") == "false"

internal val Project.isMockativeEnabled: Boolean
    get() = findProperty("io.mockative.enabled") == "true"

internal val Project.isRunningVerificationTask: Boolean
    get() = gradle.taskGraph.allTasks.any { it is VerificationTask }

internal val Project.isRunningTestPrefix: Boolean
    get() = gradle.startParameter.taskNames.any { it.startsWith("test") }

internal val Project.isRunningTestSuffix: Boolean
    get() = gradle.startParameter.taskNames.any { it.endsWith("Test") }

internal val Project.isRunningTestsSuffix: Boolean
    get() = gradle.startParameter.taskNames.any { it.endsWith("Tests") }

internal val Project.isMultiplatform: Boolean
    get() = kotlinExtension is KotlinMultiplatformExtension

internal fun Project.addJVMDependencies(sourceSetName: String, reason: String? = null) {
    val sourceSet = kotlinExtension.sourceSets.firstOrNull { it.name == sourceSetName } ?: return

    info("Adding JVM runtime dependencies to source set '${sourceSet.name}'${reason?.let { " as a result of $it" } ?: ""}")

    sourceSet.dependencies {
        implementation(kotlin("reflect"))
        implementation("org.objenesis:objenesis:3.3")
        implementation("org.javassist:javassist:3.29.2-GA")
    }
}
