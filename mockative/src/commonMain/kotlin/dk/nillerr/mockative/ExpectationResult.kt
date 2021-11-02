package io.mockative

/**
 * Represents the result of an expectation.
 *
 * @param T the type being mocked
 */
sealed class ExpectationResult<T> {
    /**
     * Represents a constant result of an expectation.
     *
     * @param value the value to return on an invocation.
     * @param T the type being mocked
     */
    data class Constant<T>(val value: Any?) : ExpectationResult<T>()

    /**
     * Represents the invocation of a block as the result of an expectation.
     *
     * @param block the block to invoke on an invocation.
     * @param T the type being mocked
     */
    data class Immediate<T>(val block: T.(args: Array<out Any?>) -> Any?) : ExpectationResult<T>()

    /**
     * Represents the invocation of a suspend block as the result of an expectation.
     *
     * @param block the suspend block to invoke on an invocation.
     * @param T the type being mocked
     */
    data class Suspended<T>(val block: suspend T.(args: Array<out Any?>) -> Any?) : ExpectationResult<T>()
}