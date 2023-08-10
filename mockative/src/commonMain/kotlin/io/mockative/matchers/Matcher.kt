package io.mockative.matchers

interface Matcher<T> {
    val defaultValue: T

    fun matches(value: Any?): Boolean
}
