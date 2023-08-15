package io.mockative

class SuspendResultBuilder<R>(
    private val mock: Mockable,
    private val expectation: Expectation
) : AnySuspendResultBuilder<R> {
    fun invokes(block: suspend (arguments: Array<Any?>) -> R) {
        mock.addSuspendStub(OpenSuspendStub(expectation, block))
    }

    fun invokesMany(blocks: List<suspend (arguments: Array<Any?>) -> R>) {
        mock.addSuspendStub(ClosedSuspendStub(expectation, blocks))
    }

    override fun invokes(block: suspend () -> R) = invokes { _ -> block() }

    override fun invokesMany(vararg blocks: suspend () -> R) = invokesMany(blocks.map { block -> { _ -> block() } })
}
