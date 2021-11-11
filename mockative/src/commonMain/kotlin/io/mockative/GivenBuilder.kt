package io.mockative

fun <T : Any> given(receiver: T): GivenBuilder<T> = GivenBuilder(receiver)

class GivenBuilder<T : Any>(private val receiver: T) {
    suspend fun <R> coroutine(block: suspend T.() -> R): SuspendResultBuilder<R> {
        val mock = receiver.asMockable()
        val invocation = mock.record(block)
        val expectation = invocation.toExpectation()
        return SuspendResultBuilder(mock, expectation)
    }

    fun <R> invocation(block: T.() -> R): ResultBuilder<R> {
        val mock = receiver.asMockable()
        val invocation = mock.record(block)
        val expectation = invocation.toExpectation()
        return ResultBuilder(mock, expectation)
    }
}
