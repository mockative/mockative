package io.mockative

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * The base class of a generated mock class.
 */
abstract class Mocked<T : Any> {

    /**
     * Using the unconfined dispatcher for suspending functions ensures we can record the
     * [Invocation] on the [expectation].
     */
    private val dispatcher = Dispatchers.Unconfined
    internal val scope = CoroutineScope(dispatcher + SupervisorJob())

    internal var expectations = mutableListOf<Expectation<T>>()
    internal var expectation: Expectation<T>? = null

    protected fun <R> mockGetter(property: String): R = mock("\$get_$property")

    protected fun mockSetter(property: String, value: Any?) = mock<Unit>("\$set_$property", value)

    @Suppress("UNCHECKED_CAST")
    protected suspend fun <R> mockSuspend(method: String, vararg args: Any?): R {
        val invocation = Invocation(method, listOf(*args))

        val builder = expectation
        if (builder != null) {
            // This path is called during a call to `given`
            val existingExpectation = findExpectationOrNull(invocation)
            if (existingExpectation != null) {
                expectations.remove(existingExpectation)
            }

            throw MockingInProgressError(invocation)
        } else {
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

        val builder = expectation
        if (builder != null) {
            // This path is called during a call to `given`
            val existingExpectation = findExpectationOrNull(invocation)
            if (existingExpectation != null) {
                expectations.remove(existingExpectation)
            }

            throw MockingInProgressError(invocation)
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

    private fun findExpectation(invocation: Invocation): Expectation<T> {
        return findExpectationOrNull(invocation) ?: throw MissingExpectationError(this, invocation)
    }

    private fun findExpectationOrNull(invocation: Invocation) =
        expectations.firstOrNull { it.invocation.matches(invocation) }

    private fun resultOf(expectation: Expectation<T>, invocation: Invocation): ExpectationResult<T> {
        return expectation.result ?: throw MissingExpectationError(this, invocation)
    }
}

