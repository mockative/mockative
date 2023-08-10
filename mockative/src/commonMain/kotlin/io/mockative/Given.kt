package io.mockative

fun <R> given(block: () -> R): ResultBuilder<R> {
    val (mock, invocation) = Mockable.record(block)
    val expectation = invocation.toExpectation()
    return ResultBuilder(mock, expectation)
}

suspend fun <R> coGiven(block: suspend () -> R): SuspendResultBuilder<R> {
    val (mock, invocation) = Mockable.record(block)
    val expectation = invocation.toExpectation()
    return SuspendResultBuilder(mock, expectation)
}
