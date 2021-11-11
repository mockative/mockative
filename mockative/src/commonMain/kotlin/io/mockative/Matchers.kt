package io.mockative

import io.mockative.matchers.AnyMatcher
import io.mockative.matchers.ComparableMatcher
import io.mockative.matchers.EqualsMatcher
import io.mockative.matchers.OneOfMatcher

inline fun <reified T> any(): AnyMatcher<T> {
    return AnyMatcher(T::class)
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
