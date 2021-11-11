package io.mockative

fun <T : Any, R> given(receiver: T, block: T.() -> R): ResultBuilder<R> {
    return given(receiver).invocation(block)
}

class ResultBuilder<R>(
    private val mock: Mockable,
    private val expectation: Expectation
) : AnyResultBuilder<R> {
    fun then(block: (arguments: Array<Any?>) -> R) {
        mock.addBlockingStub(BlockingStub(expectation, block))
    }

    override fun thenInvoke(block: () -> R) = then { block() }
}