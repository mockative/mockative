package io.mockative

interface AnyResultBuilder<R> {
    fun thenInvoke(block: () -> R)

    fun thenReturn(value: R) = thenInvoke { value }

    fun thenThrow(throwable: Throwable) = thenInvoke { throw throwable }
}

fun AnyResultBuilder<Unit>.thenDoNothing() = thenInvoke { }
