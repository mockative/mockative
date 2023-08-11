package io.mockative

class ResultBuilder<R>(
    private val mock: Mockable,
    private val expectation: Expectation
) : AnyResultBuilder<R> {
    fun invokes(block: (arguments: Array<Any?>) -> R) {
        mock.addBlockingStub(BlockingStub(expectation, block))
    }

    @Deprecated("Replaced by the `invokes` function", replaceWith = ReplaceWith("invokes"))
    fun then(block: (arguments: Array<Any?>) -> R) = invokes(block)

    override fun invokes(block: () -> R) = invokes { _ -> block() }
}
