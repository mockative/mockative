package io.mockative.matchers

import kotlin.reflect.KClass

class ComparableMatcher<T : Comparable<T>>(
    private val type: KClass<T>,
    private val operand: T,
    private val operator: String,
    private val comparison: (T, T) -> Boolean
) : Matcher<T> {
    override fun matches(value: Any?): Boolean {
        return type.isInstance(value) && comparison(value as T, operand)
    }

    override fun toString(): String {
        return "$operator $operand"
    }
}