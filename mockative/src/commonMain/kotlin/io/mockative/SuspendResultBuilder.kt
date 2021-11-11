package io.mockative

fun <T : Any, R> givenSuspend(receiver: T, block: suspend T.() -> R): SuspendResultBuilder<R> {
    val mock = receiver as Mockable
    val invocation = mock.record(block)
    val expectation = invocation.toExpectation()
    return SuspendResultBuilder(mock, expectation)
}

class SuspendResultBuilder<R>(private val mock: Mockable, private val expectation: Expectation) {
    fun then(block: suspend (arguments: Array<Any?>) -> R) {
        mock.addSuspendStub(SuspendStub(expectation, block))
    }
}