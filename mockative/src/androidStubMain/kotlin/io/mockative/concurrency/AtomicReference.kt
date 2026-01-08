package io.mockative.concurrency

import java.util.concurrent.atomic.AtomicReference

internal actual class AtomicReference<T> actual constructor(value: T) {
    private val ref = AtomicReference(value)

    actual var value: T
        get() = ref.get()
        set(value) { ref.set(value) }

    actual fun compareAndSet(expected: T, new: T): Boolean {
        return ref.compareAndSet(expected, new)
    }
}