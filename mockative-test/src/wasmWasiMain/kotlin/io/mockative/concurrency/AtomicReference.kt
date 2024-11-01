package io.mockative.concurrency

internal actual class AtomicReference<T> actual constructor(actual var value: T) {
    actual fun compareAndSet(expected: T, new: T): Boolean {
        if (value == expected) {
            value = new
            return true
        }

        return false
    }
}
