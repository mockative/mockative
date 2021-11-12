package io.mockative

interface AnySuspendResultBuilder<R> {
    fun thenInvoke(block: suspend () -> R)

    fun thenReturn(value: R) = thenInvoke { value }

    fun thenThrow(throwable: Throwable) = thenInvoke { throw throwable }
}

fun AnySuspendResultBuilder<Unit>.thenDoNothing() = thenInvoke { }
