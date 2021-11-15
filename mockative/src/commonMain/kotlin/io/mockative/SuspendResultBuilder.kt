package io.mockative

class SuspendResultBuilder<R>(
    private val mock: Mockable,
    private val expectation: Expectation
) : AnySuspendResultBuilder<R> {
    fun then(block: suspend (arguments: Array<Any?>) -> R) {
        mock.addSuspendStub(SuspendStub(expectation, block))
    }

    override fun thenInvoke(block: suspend () -> R) = then { block() }
}