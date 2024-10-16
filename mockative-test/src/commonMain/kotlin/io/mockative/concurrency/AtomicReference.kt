package io.mockative.concurrency

internal expect class AtomicReference<T>(value: T) {
    var value: T

    fun compareAndSet(expected: T, new: T): Boolean
}
