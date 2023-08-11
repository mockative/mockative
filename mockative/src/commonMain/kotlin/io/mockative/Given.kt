package io.mockative

//fun <T : Any> given(receiver: T): GivenBuilder<T> = GivenBuilder(receiver)

fun <R> every(block: () -> R): ResultBuilder<R> {
    try {
        val (mock, invocation) = Mockable.record(block)
        val expectation = invocation.toExpectation()
        return ResultBuilder(mock, expectation)
    } finally {
        Matchers.clear()
    }
}

suspend fun <R> coEvery(block: suspend () -> R): SuspendResultBuilder<R> {
    try {
        val (mock, invocation) = Mockable.record(block)
        val expectation = invocation.toExpectation()
        return SuspendResultBuilder(mock, expectation)
    } finally {
        Matchers.clear()
    }
}
