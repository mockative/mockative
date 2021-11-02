package io.mockative

class ExpectationBuilder<T : Any, R>(override val instance: T) : Expectation<T> {
    override lateinit var invocation: Invocation

    override var result: ExpectationResult<T>? = null
    override var invocations: Int = 0

    /**
     * Stubs the expectation by returning the specified [value].
     *
     * @param value the value to return on an invocation of the expectation.
     */
    fun thenReturn(value: R) {
        result = ExpectationResult.Constant(value)
    }

    /**
     * Stubs the expectation by invoking the specified [block] on invocations.
     *
     * @param block the block to invoke on an invocation of the expectation.
     */
    fun then(block: T.(args: Array<out Any?>) -> R) {
        result = ExpectationResult.Immediate(block)
    }

    /**
     * Stubs the expectation by invoking the specified [block] on invocations.
     *
     * @param block the block to invoke on an invocation of the expectation.
     */
    fun thenSuspend(block: suspend T.(args: Array<out Any?>) -> R) {
        result = ExpectationResult.Suspended(block)
    }

    /**
     * Stubs the expectation by throwing the specified [error] on invocations.
     *
     * @param error the error to throw on an invocation of the expectation.
     */
    fun thenThrow(error: Throwable) = then { throw error }
}

/**
 * Stubs the expectation by returning [Unit] on invocations.
 */
fun <T : Any> ExpectationBuilder<T, Unit>.thenDoNothing() = thenReturn(Unit)
