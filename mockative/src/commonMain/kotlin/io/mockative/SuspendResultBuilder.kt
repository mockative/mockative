package io.mockative

suspend fun <T : Any, R> givenCoroutine(receiver: T, block: suspend T.() -> R): SuspendResultBuilder<R> {
    return given(receiver).coroutine(block)
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