package dk.nillerr.mockative

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * The base class of a generated mock class
 */
abstract class Mocked {

    private val dispatcher = Dispatchers.Unconfined
    internal val scope = CoroutineScope(dispatcher + SupervisorJob())

    internal var expectations = mutableListOf<Expectation>()
    internal var expectation: Expectation? = null

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

            builder.invocation = invocation

            throw MockingInProgressError()
        } else {
            // This path is called during a call to the method
            val expectation = findExpectation(invocation)

            val returnValue = when (val result = resultOf(expectation, invocation)) {
                is ExpectationResult.Constant -> result.value as R
                is ExpectationResult.Immediate -> result.block(args) as R
                is ExpectationResult.Suspended -> result.block(args) as R
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

            builder.invocation = invocation

            throw MockingInProgressError()
        } else {
            // This path is called during a call to the method
            val expectation = findExpectation(invocation)

            val returnValue = when (val result = resultOf(expectation, invocation)) {
                is ExpectationResult.Constant -> result.value as R
                is ExpectationResult.Immediate -> result.block(args) as R
                is ExpectationResult.Suspended -> throw SuspendedExpectationOnNonSuspendingFunctionError(this, invocation)
            }

            expectation.invocations += 1

            return returnValue
        }
    }

    private fun findExpectation(invocation: Invocation): Expectation {
        return findExpectationOrNull(invocation) ?: throw MissingExpectationError(this, invocation)
    }

    private fun findExpectationOrNull(invocation: Invocation) =
        expectations.firstOrNull { it.invocation.matches(invocation) }

    private fun resultOf(expectation: Expectation, invocation: Invocation): ExpectationResult {
        return expectation.result ?: throw MissingExpectationError(this, invocation)
    }
}

