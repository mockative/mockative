package io.mockative

class SuspendResultBuilder<R>(
    private val mock: MockState,
    private val expectation: Expectation
) {
    infix fun invokes(block: suspend (arguments: Array<Any?>) -> R) {
        mock.addSuspendStub(OpenSuspendStub(expectation, block))
    }

    fun invokesMany(blocks: List<suspend (arguments: Array<Any?>) -> R>) {
        mock.addSuspendStub(ClosedSuspendStub(expectation, blocks))
    }

    fun invokesMany(vararg blocks: suspend () -> R) = invokesMany(blocks.map { block -> { _ -> block() } })

    infix fun returns(value: R) = invokes { value }

    fun returnsMany(vararg values: R) = invokesMany(values.map { value -> { value } })

    infix fun throws(throwable: Throwable) = invokes { throw throwable }

    fun throwsMany(vararg throwables: Throwable) = invokesMany(throwables.map { throwable -> { throw throwable } })
}

fun SuspendResultBuilder<Unit>.doesNothing() = invokes { }
