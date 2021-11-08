package io.mockative.concurrency

import kotlin.native.concurrent.ThreadLocal

/**
 * Wraps a potentially null value in an object to prevent storing `null` values in the [Map] of
 * [confinedValues].
 */
private data class ConfinedValue(val value: Any?)

/**
 * Contains the values of each open [ConfinedValue].
 *
 * While this property is marked with [ThreadLocal], it is effectively never shared with other
 * threads, but must be annotated to remain mutable.
 */
@ThreadLocal
private val confinedValues = mutableMapOf<Confined<*>, ConfinedValue>()

/**
 * A confined reference to a (mutable) Kotlin object. By using the [invoke] method (or operator) to
 * perform actions on the value held by this object, all access and modification of the wrapped
 * value is perform on the same thread, preventing the value from being frozen, thus keeping it
 * mutable.
 *
 * Be cautious of nesting calls to [confined], and thus access to [Confined] values, as it
 * can cause segmentation faults in Kotlin/Native.
 *
 * Users of a [Confined] value should call [close] when the value is no longer used, in order
 * to free up the memory.
 */
class Confined<T>(private val initialValue: () -> T) : Closeable {
    init {
        confined {
            val value = initialValue()
            val synchronizedValue = ConfinedValue(value)
            confinedValues.put(this, synchronizedValue)
        }
    }

    operator fun <R> invoke(block: T.() -> R): R {
        return confined {
            val synchronizedValue = confinedValues[this]
                ?: throw Error("The synchronized value has already been disposed")

            @Suppress("UNCHECKED_CAST")
            val value = synchronizedValue.value as T

            block(value)
        }
    }

    override fun close() {
        confined {
            confinedValues.remove(this)
        }
    }
}