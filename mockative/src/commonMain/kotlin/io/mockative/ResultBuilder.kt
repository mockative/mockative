package io.mockative

class ResultBuilder<R>(
    private val mock: Mockable,
    private val expectation: Expectation
) : AnyResultBuilder<R> {
    fun then(block: (arguments: Array<Any?>) -> R) {
        mock.addBlockingStub(BlockingStub(expectation, block))
    }

    override fun thenInvoke(block: () -> R) = then { block() }
}