package io.mockative

fun verifyNoUnverifiedExpectations(receiver: Any) {
    Mockable.mockable(receiver).confirmVerified()
}

fun verifyNoUnmetExpectations(receiver: Any) {
    Mockable.mockable(receiver).verifyNoUnmetExpectations()
}

fun <R> verify(block: () -> R) {
    verification(block).wasInvoked()
}

fun <R> verify(exactly: Int, block: () -> R) {
    verification(block).wasInvoked(exactly)
}

fun <R> verify(
    atLeast: Int? = null,
    atMost: Int? = null,
    block: () -> R
) {
    verification(block).wasInvoked(atLeast, atMost)
}

suspend fun <R> coVerify(block: suspend () -> R) {
    verification(block).wasInvoked()
}

suspend fun <R> coVerify(exactly: Int, block: suspend () -> R) {
    verification(block).wasInvoked(exactly)
}

suspend fun <R> coVerify(
    atLeast: Int? = null,
    atMost: Int? = null,
    block: suspend () -> R
) {
    verification(block).wasInvoked(atLeast, atMost)
}

private fun <R> verification(block: () -> R): Verification {
    val (mock, invocation) = Mockable.record(block)
    val expectation = invocation.toExpectation()
    return Verification(mock, expectation)
}

private suspend fun <R> verification(block: suspend () -> R): Verification {
    val (mock, invocation) = Mockable.record(block)
    val expectation = invocation.toExpectation()
    return Verification(mock, expectation)
}
