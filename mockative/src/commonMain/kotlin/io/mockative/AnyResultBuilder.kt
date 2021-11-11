package io.mockative

interface AnyResultBuilder<R> {
    fun thenInvoke(block: () -> R)

    fun thenReturn(value: R) = thenInvoke { value }

    fun thenThrow(error: Error) = thenInvoke { throw error }
}

fun AnyResultBuilder<Unit>.thenDoNothing() = thenInvoke { }
