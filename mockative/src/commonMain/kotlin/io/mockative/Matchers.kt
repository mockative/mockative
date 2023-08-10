package io.mockative

import io.mockative.matchers.*
import kotlin.jvm.JvmName
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object Matchers {
    private val matchers = mutableListOf<Matcher<*>>()

    val size: Int
        get() = matchers.size

    fun <T> enqueue(matcher: Matcher<T>): T {
        matchers.add(matcher)
        return matcher.defaultValue
    }

    fun dequeue(): Matcher<*> {
        return matchers.removeFirst()
    }

    fun clear() {
        matchers.clear()
    }
}

inline fun <reified T> error(): T {
    throw IllegalStateException("This function may not be called.")
}

inline fun <reified T> any(defaultValue: T): T {
    return Matchers.enqueue(AnyMatcher(defaultValue))
}

inline fun <reified T> eq(value: T): T {
    return Matchers.enqueue(EqualsMatcher(value))
}

inline fun <reified T> oneOf(vararg values: T): T {
    return Matchers.enqueue(OneOfMatcher(values.toList()))
}

inline fun <reified T : Comparable<T>> gt(value: T): T {
    return Matchers.enqueue(ComparableMatcher(T::class, value, ">") { a, b -> a > b })
}

inline fun <reified T : Comparable<T>> gte(value: T): T {
    return Matchers.enqueue(ComparableMatcher(T::class, value, ">=") { a, b -> a >= b })
}

inline fun <reified T : Comparable<T>> lt(value: T): T {
    return Matchers.enqueue(ComparableMatcher(T::class, value, "<") { a, b -> a < b })
}

inline fun <reified T : Comparable<T>> lte(value: T): T {
    return Matchers.enqueue(ComparableMatcher(T::class, value, "<=") { a, b -> a <= b })
}

inline fun <reified T> matching(defaultValue: T, noinline predicate: (T) -> Boolean): T {
    return Matchers.enqueue(PredicateMatcher(T::class, defaultValue, predicate))
}
