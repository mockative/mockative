package io.mockative

class ResultBuilder<R>(
    private val mock: Mockable,
    private val expectation: Expectation
) {
    fun invokes(block: (arguments: Array<Any?>) -> R) {
        mock.addBlockingStub(OpenBlockingStub(expectation, block))
    }

    fun invokesMany(blocks: List<(arguments: Array<Any?>) -> R>) {
        mock.addBlockingStub(ClosedBlockingStub(expectation, blocks))
    }

    fun invokesMany(vararg blocks: () -> R) = invokesMany(blocks.map { block -> { _ -> block() } })

    fun returns(value: R) = invokes { value }

    fun returnsMany(vararg values: R) = invokesMany(values.map { value -> { value } })

    fun throws(throwable: Throwable) = invokes { throw throwable }

    fun throwsMany(vararg throwables: Throwable) = invokesMany(throwables.map { throwable -> { throw throwable } })
}

fun ResultBuilder<Unit>.doesNothing() = invokes { }
