package io.mockative.concurrency

import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.freeze

internal actual class AtomicReference<T> actual constructor(value: T) {
    private val ref = AtomicReference(value)

    actual var value: T
        get() = ref.value
        set(value) { ref.value = value.freeze() }

    actual fun compareAndSet(expected: T, new: T): Boolean {
        return ref.compareAndSet(expected, new.freeze())
    }
}