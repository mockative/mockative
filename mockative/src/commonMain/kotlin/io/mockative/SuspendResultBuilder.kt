package io.mockative

fun <T : Any, R> givenCoroutine(receiver: T, block: suspend T.() -> R): SuspendResultBuilder<R> {
    val mock = receiver.asMockable()
    val invocation = mock.record(block)
    val expectation = invocation.toExpectation()
    return SuspendResultBuilder(mock, expectation)
}

class SuspendResultBuilder<R>(
    private val mock: Mockable,
    private val expectation: Expectation
) : AnySuspendResultBuilder<R> {
    fun then(block: suspend (arguments: Array<Any?>) -> R) {
        mock.addSuspendStub(SuspendStub(expectation, block))
    }

    override fun thenInvoke(block: suspend () -> R) = then { block() }
}