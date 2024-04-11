package io.mockative

private inline fun <R> suspendFun(crossinline block: () -> R): suspend () -> R = { block() }

interface AnySuspendResultBuilder<R> {
    fun invokes(block: suspend () -> R)

    fun invokesMany(vararg blocks: suspend () -> R)

    fun returns(value: R) = invokes { value }

    fun returnsMany(vararg values: R) = invokesMany(*values.map { suspendFun { it } }.toTypedArray())

    fun throws(throwable: Throwable) = invokes { throw throwable }

    fun throwsMany(vararg throwables: Throwable) = invokesMany(*throwables.map { suspendFun { throw it } }.toTypedArray())
}
