@file:Suppress("UnusedImport")

package io.mockative

// This import must be kept otherwise compilation fails when multimodule mode is activated
import io.mockative.Verification

internal fun verifyNoUnverifiedExpectations(receiver: Any) {
    MockState.mock(receiver).confirmVerified()
}

internal fun verifyNoUnmetExpectations(receiver: Any) {
    MockState.mock(receiver).verifyNoUnmetExpectations()
}

internal fun <R> verify(block: () -> R): Verification {
    val (mock, invocation) = MockState.record(block)
    val expectation = invocation.toExpectation()
    return Verification(mock, expectation)
}

internal suspend fun <R> coVerify(block: suspend () -> R): Verification {
    val (mock, invocation) = MockState.record(block)
    val expectation = invocation.toExpectation()
    return Verification(mock, expectation)
}
