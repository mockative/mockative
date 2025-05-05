package io.mockative.matchers

interface Matcher<T> {
    val placeholder: T

    fun matches(value: Any?): Boolean
}
