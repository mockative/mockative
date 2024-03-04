package io.mockative

fun verifyNoUnverifiedExpectations(receiver: Any) {
    Mockable.mockable(receiver).confirmVerified()
}

fun verifyNoUnmetExpectations(receiver: Any) {
    Mockable.mockable(receiver).verifyNoUnmetExpectations()
}

fun <R> verify(block: () -> R): Verification {
    val (mock, invocation) = Mockable.record(block)
    val expectation = invocation.toExpectation()
    return Verification(mock, expectation)
}

suspend fun <R> coVerify(block: suspend () -> R): Verification {
    val (mock, invocation) = Mockable.record(block)
    val expectation = invocation.toExpectation()
    return Verification(mock, expectation)
}
