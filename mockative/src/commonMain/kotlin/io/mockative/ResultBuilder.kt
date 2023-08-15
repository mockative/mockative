package io.mockative

class ResultBuilder<R>(
    private val mock: Mockable,
    private val expectation: Expectation
) : AnyResultBuilder<R> {
    fun invokes(block: (arguments: Array<Any?>) -> R) {
        mock.addBlockingStub(OpenBlockingStub(expectation, block))
    }

    fun invokesMany(blocks: List<(arguments: Array<Any?>) -> R>) {
        mock.addBlockingStub(ClosedBlockingStub(expectation, blocks))
    }

    override fun invokes(block: () -> R) = invokes { _ -> block() }

    override fun invokesMany(vararg blocks: () -> R) = invokesMany(blocks.map { block -> { _ -> block() } })
}
