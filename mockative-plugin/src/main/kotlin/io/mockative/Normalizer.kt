/**
 * This file contains the `Normalizer` class and its builder, which are used to create
 * normalized file paths. This is particularly useful for ensuring consistent path
 * structures across different modules and build environments.
 */
package io.mockative

/**
 * A utility class that normalizes a given path statement.
 *
 * The normalization process transforms a path into a standardized format based on a
 * base name, a module name, and a separator. This helps in creating predictable
 * and consistent file paths or package names for generated code.
 *
 * @property basename The base components of the path.
 * @property moduleName The name of the module to be included in the path.
 * @property separator The character used to separate path components.
 */
class Normalizer internal constructor(
    private val basename: List<String>,
    private val moduleName: String,
    private val exclusions: List<String> = emptyList(),
    private val isApplicable: Boolean = false,
    separator: Char,
) {
    private val separator: String = separator.toString()

    /**
     * Normalizes the given path statement by injecting a module name immediately after the base path.
     *
     * This function first verifies that the input `statement` starts with the configured `basename`.
     * It then reconstructs the path by concatenating the `basename`, the `moduleName`, and the
     * remainder of the original statement.
     *
     * For example, with a `basename` of `["com", "example"]`, a `moduleName` of `"core"`,
     * and a `separator` of `.`, the statement `com.example.feature.ui.MyViewModel` would be
     * normalized to `com.example.core.feature.ui.MyViewModel`.
     *
     * @param statement The path statement to normalize, which must start with the `basename`.
     * @return The normalized path string with the module name injected.
     * @throws IllegalArgumentException if the `statement` does not start with the `basename`.
     */
    fun normalize(statement: String): String {
        if (exclusions.any { statement.contains(it) } || !isApplicable) return statement
        val parts = statement.split(separator)
        require(
            parts.take(basename.size).containsAll(basename)
        ) { "The statement '$statement' should start with the whole basename" }
        val suffixParts = parts.drop(basename.size)
        val suffix =
            if (suffixParts.isEmpty()) "" else "$separator${suffixParts.joinToString(separator)}"
        val normalizedBasename = basename.joinToString(separator)
        return "$normalizedBasename$separator$moduleName$suffix"
    }
}

/**
 * A builder for creating [Normalizer] instances.
 *
 * This builder provides a DSL-style approach for constructing a `Normalizer`
 * with the required `basename`, `moduleName`, and `separator`.
 */
class NormalizerBuilder internal constructor() {
    /** The base components of the path. */
    lateinit var basename: List<String>

    /** The name of the module to be included in the path. */
    lateinit var moduleName: String

    /** The list of exclusions for the path. */
    var exclusions: MutableList<String> = mutableListOf()

    /** The character used to separate path components. */
    var separator: Char? = null

    /**
     * Determines whether the module name should be applied to the normalized statement or not
     */
    var isApplicable: Boolean = false

    /**
     * Builds and returns a [Normalizer] instance.
     *
     * @throws IllegalArgumentException if the separator is not set.
     */
    fun build(): Normalizer {
        require(separator != null) { "Missing separator in builder definition" }
        return Normalizer(
            basename = basename,
            moduleName = moduleName,
            exclusions = exclusions,
            separator = separator!!,
            isApplicable = isApplicable
        )
    }
}

/**
 * A DSL entry point for creating a [Normalizer].
 *
 * @param block A lambda with the [NormalizerBuilder] as its receiver to configure the normalizer.
 * @return A configured [Normalizer] instance.
 */
fun Normalizer(block: NormalizerBuilder.() -> Unit): Normalizer =
    NormalizerBuilder()
        .apply(block)
        .build()
