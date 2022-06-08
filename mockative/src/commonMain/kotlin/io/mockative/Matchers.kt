package io.mockative

import io.mockative.matchers.*
import kotlin.reflect.KClass

inline fun wildcard(): WildcardMatcher<*> {
    return WildcardMatcher<Any?>()
}

inline fun <reified T> anything(): AnythingMatcher<T> {
    return AnythingMatcher()
}

inline fun <reified T> any(): AnyMatcher<T> {
    return AnyMatcher(T::class)
}

fun <R : Any, T : R> anyInstanceOf(type: KClass<T>): AnyMatcher<R> {
    return AnyMatcher(type)
}

inline fun <reified T> eq(value: T): EqualsMatcher<T> {
    return EqualsMatcher(value)
}

inline fun <reified T> oneOf(vararg values: T): OneOfMatcher<T> {
    return OneOfMatcher(values.toList())
}

inline fun <reified T : Comparable<T>> gt(value: T): ComparableMatcher<T> {
    return ComparableMatcher(T::class, value, ">") { a, b -> a > b }
}

inline fun <reified T : Comparable<T>> gte(value: T): ComparableMatcher<T> {
    return ComparableMatcher(T::class, value, ">=") { a, b -> a >= b }
}

inline fun <reified T : Comparable<T>> lt(value: T): ComparableMatcher<T> {
    return ComparableMatcher(T::class, value, "<") { a, b -> a < b }
}

inline fun <reified T : Comparable<T>> lte(value: T): ComparableMatcher<T> {
    return ComparableMatcher(T::class, value, "<=") { a, b -> a <= b }
}

inline fun <reified T> matching(noinline predicate: (T) -> Boolean): PredicateMatcher<T> {
    return PredicateMatcher(T::class, predicate)
}
