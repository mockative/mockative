package io.mockative

interface AnySuspendResultBuilder<R> {
    fun thenInvoke(block: suspend () -> R)

    fun thenReturn(value: R) = thenInvoke { value }

    fun thenThrow(error: Error) = thenInvoke { throw error }
}

fun AnySuspendResultBuilder<Unit>.thenDoNothing() = thenInvoke { }
