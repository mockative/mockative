package io.mockative

import io.mockative.concurrency.Confined

internal class ExpectationBuilderData<T : Any> {
    lateinit var invocation: Invocation

    var result: ExpectationResult<T>? = null
    var invocations: Int = 0
}

class ExpectationBuilder<T : Any, R>(override val instance: T) : Expectation<T> {
    internal val data = Confined { ExpectationBuilderData<T>() }

    override var invocation: Invocation
        get() = data { invocation }
        set(value) = data { invocation = value }

    override var result: ExpectationResult<T>?
        get() = data { result }
        set(value) = data { result = value }

    override var invocations: Int
        get() = data { invocations }
        set(value) = data { invocations = value }

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

    override fun close() {
        data.close()
    }
}

/**
 * Stubs the expectation by returning [Unit] on invocations.
 */
fun <T : Any> ExpectationBuilder<T, Unit>.thenDoNothing() = thenReturn(Unit)
