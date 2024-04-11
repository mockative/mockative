package io.mockative

interface AnyResultBuilder<R> {
    fun invokes(block: () -> R)

    fun invokesMany(vararg blocks: () -> R)

    fun returns(value: R) = invokes { value }

    fun returnsMany(vararg values: R) = invokesMany(*values.map { { it } }.toTypedArray())

    fun throws(throwable: Throwable) = invokes { throw throwable }

    fun throwsMany(vararg throwables: Throwable) = invokesMany(*throwables.map { { throw it } }.toTypedArray())
}
