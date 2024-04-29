package io.mockative

class SuspendResultBuilder<R>(
    private val mock: Mockable,
    private val expectation: Expectation
) {
    fun invokes(block: suspend (arguments: Array<Any?>) -> R) {
        mock.addSuspendStub(OpenSuspendStub(expectation, block))
    }

    fun invokesMany(blocks: List<suspend (arguments: Array<Any?>) -> R>) {
        mock.addSuspendStub(ClosedSuspendStub(expectation, blocks))
    }

    fun returns(value: R) = invokes { value }

    fun returnsMany(vararg values: R) = invokesMany(values.map { value -> { value } })

    fun throws(throwable: Throwable) = invokes { throw throwable }

    fun throwsMany(vararg throwables: Throwable) = invokesMany(throwables.map { throwable -> { throw throwable } })
}

fun SuspendResultBuilder<Unit>.doesNothing() = invokes { }
