package io.mockative.concurrency

import kotlin.reflect.KProperty

internal class AtomicRef<T>(value: T) {
    private val ref = AtomicReference(value)

    var value: T
        get() = ref.value
        set(value) { ref.value = value }

    fun compareAndSet(expected: T, new: T): Boolean = ref.compareAndSet(expected, new)

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun <T> atomic(value: T): AtomicRef<T> = AtomicRef(value)
