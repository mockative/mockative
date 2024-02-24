package io.mockative.concurrency

internal actual class AtomicReference<T> actual constructor(actual var value: T) {
    actual fun compareAndSet(expected: T, new: T): Boolean {
        value = new
        return true
    }
}
