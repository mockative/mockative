package io.mockative

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import javax.inject.Inject

/**
 * Configures the Mockative Gradle plugin.
 *
 * This extension is used in your `build.gradle.kts` file to customize the behavior of the
 * Mockative code generation and runtime.
 *
 * Example usage in `build.gradle.kts`:
 * ```kotlin
 * mockative {
 *     stubsUnitByDefault.set(true)
 *     excludeKotlinDefaultMembers.set(false)
 * }
 * ```
 */
abstract class MockativeExtension @Inject constructor(objects: ObjectFactory) {
    internal val excludeMembers: SetProperty<String> = objects.setProperty(String::class.java)

    /**
     * Specifies whether to automatically stub default Kotlin members (`equals`, `hashCode`, `toString`).
     *
     * When `true` (the default), Mockative generates stubs for these functions, allowing them to be
     * verified like any other member. When `false`, the original implementations from `Any` are
     * preserved.
     */
    val excludeKotlinDefaultMembers: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

    /**
     * Specifies whether to automatically create default stubs for functions returning `Unit`.
     *
     * When `true` (the default), any function on a mock that returns `Unit` will be stubbed
     * automatically to do nothing. This avoids the need to manually set up `every` blocks for
     * every `Unit` function. When `false`, calling a `Unit` function without an explicit stub
     * will result in an error.
     */
    val stubsUnitByDefault: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

    /**
     * Configures the code generator for a multi-module project structure.
     *
     * When set to `true`, Mockative alters the package name of generated mock classes by appending
     * the current Gradle module's name. This prevents DEXing errors in Android projects that arise
     * from duplicate classes when the same mockable type is used across different modules.
     *
     * For example, a mock for `com.example.Service` in the `:feature:auth` module would be
     * generated in `com.example.feature.auth` instead of `com.example`.
     *
     * This property is `false` by default to maintain backward compatibility for existing
     * single-module projects.
     */
    val isMultimodule: Property<Boolean> = objects.property(Boolean::class.java).convention(false)

    /**
     * Applies fine-grained configuration for members within a specific package, allowing for
     * targeted exclusion of functions or properties from the mocking process.
     *
     * This function provides a scope where you can exclude members at the package level or
     * drill down to specific types within that package.
     *
     * ### Example: Excluding a top-level function in a package
     *
     * ```kotlin
     * mockative {
     *     forPackage("com.example.api") {
     *         exclude("myTopLevelFunction")
     *     }
     * }
     * ```
     * This will exclude `com.example.api.myTopLevelFunction` from being mocked.
     *
     * ### Example: Excluding a member of a specific type
     *
     * ```kotlin
     * mockative {
     *     forPackage("com.example.api") {
     *         type("UserService") {
     *             exclude("deleteUser")
     *         }
     *     }
     * }
     * ```
     * This will exclude the `deleteUser` function on the `com.example.api.UserService` type.
     *
     * @param packageName The fully qualified name of the package to configure.
     * @param block A lambda with a [PackageExtension] receiver to define the configuration.
     *              Inside this block, you can use `exclude()` to target members of the package
     *              or `type()` to scope further into a specific class or interface.
     */
    fun forPackage(packageName: String, block: PackageExtension.() -> Unit) {
        PackageExtension(packageName, this).block()
    }
}
