package io.mockative

fun verifyNoUnverifiedExpectations(receiver: Any) {
    MockState.mock(receiver).confirmVerified()
}

fun verifyNoUnmetExpectations(receiver: Any) {
    MockState.mock(receiver).verifyNoUnmetExpectations()
}

fun <R> verify(block: () -> R): io.mockative.Verification {
    val (mock, invocation) = MockState.record(block)
    val expectation = invocation.toExpectation()
    return io.mockative.Verification(mock, expectation)
}

suspend fun <R> coVerify(block: suspend () -> R): io.mockative.Verification {
    val (mock, invocation) = MockState.record(block)
    val expectation = invocation.toExpectation()
    return io.mockative.Verification(mock, expectation)
}
