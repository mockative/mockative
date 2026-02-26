package io.mockative.concurrency

import kotlin.concurrent.AtomicReference

internal actual class AtomicReference<T> actual constructor(value: T) {
    private val ref = AtomicReference(value)

    actual var value: T
        get() = ref.value
        set(value) { ref.value = value }

    actual fun compareAndSet(expected: T, new: T): Boolean {
        return ref.compareAndSet(expected, new)
    }
}
