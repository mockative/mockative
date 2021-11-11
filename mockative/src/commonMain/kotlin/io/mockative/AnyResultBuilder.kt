package io.mockative

interface AnyResultBuilder<R> {
    fun then(block: () -> R)

    fun thenReturn(value: R) = then { value }
}

interface AnySuspendResultBuilder<R> {
    fun then(block: suspend () -> R)
}