package io.mockative

fun <T : Any, R> given(receiver: T, block: T.() -> R): ResultBuilder<R> {
    val mock = receiver as Mockable
    val invocation = mock.record(block)
    val expectation = invocation.toExpectation()
    return ResultBuilder(mock, expectation)
}

class ResultBuilder<R>(private val mock: Mockable, private val expectation: Expectation) : AnyResultBuilder<R> {
    fun then(block: (arguments: Array<Any?>) -> R) {
        mock.addBlockingStub(BlockingStub(expectation, block))
    }

    override fun then(block: () -> R) {
        mock.addBlockingStub(BlockingStub(expectation) { block() })
    }
}