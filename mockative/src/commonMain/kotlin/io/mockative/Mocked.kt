package io.mockative

import io.mockative.concurrency.Confined
import kotlinx.coroutines.*

internal class MockData<T : Any>(
    var expectations: MutableList<Expectation<T>> = mutableListOf(),
    var expectation: Expectation<T>? = null
)

/**
 * The base class of a generated mock class.
 */
abstract class Mocked<T : Any> {

    internal val data = Confined { MockData<T>() }

    init {
        mocks { add(this@Mocked) }
    }

    @Suppress("UNCHECKED_CAST")
    protected suspend fun <R> mockSuspend(method: String, vararg args: Any?): R {
        val invocation = Invocation(method, listOf(*args))

        val builder = data { expectation }
        if (builder != null) {
            println("builder != null")
            // This path is called during a call to `given`
            val existingExpectation = findExpectationOrNull(invocation)
            throw MockingInProgressError(existingExpectation, invocation)
        } else {
            println("builder == null")
            // This path is called during a call to the method
            val expectation = findExpectation(invocation)

            val returnValue = when (val result = resultOf(expectation, invocation)) {
                is ExpectationResult.Constant<T> -> result.value as R
                is ExpectationResult.Immediate<T> -> result.block.invoke(this as T, args) as R
                is ExpectationResult.Suspended<T> -> result.block.invoke(this as T, args) as R
            }

            expectation.invocations += 1

            return returnValue
        }
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <R> mock(method: String, vararg args: Any?): R {
        val invocation = Invocation(method, listOf(*args))

        val builder = data { expectation }
        if (builder != null) {
            // This path is called during a call to `given`
            val existingExpectation = findExpectationOrNull(invocation)
            if (existingExpectation != null) {
                data { expectations.remove(existingExpectation) }
            }

            throw MockingInProgressError(existingExpectation, invocation)
        } else {
            // This path is called during a call to the method
            val expectation = findExpectation(invocation)

            val returnValue = when (val result = resultOf(expectation, invocation)) {
                is ExpectationResult.Constant<T> -> result.value as R
                is ExpectationResult.Immediate<T> -> result.block.invoke(this as T, args) as R
                is ExpectationResult.Suspended<T> -> throw SuspendedExpectationOnNonSuspendingFunctionError(this, invocation)
            }

            expectation.invocations += 1

            return returnValue
        }
    }

    internal fun findExpectation(invocation: Invocation): Expectation<T> {
        return findExpectationOrNull(invocation) ?: throw MissingExpectationError(this, invocation)
    }

    internal fun findExpectationOrNull(invocation: Invocation) =
        data { expectations.firstOrNull { it.invocation.matches(invocation) } }

    private fun resultOf(expectation: Expectation<T>, invocation: Invocation): ExpectationResult<T> {
        return expectation.result ?: throw MissingExpectationError(this, invocation)
    }
}

