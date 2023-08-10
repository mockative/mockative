package io.mockative

fun verifyNoUnverifiedExpectations(receiver: Any) {
    receiver.asMockable().confirmVerified()
}

fun verifyNoUnmetExpectations(receiver: Any) {
    receiver.asMockable().verifyNoUnmetExpectations()
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
