package io.mockative.matchers

interface Matcher<in T> {
    fun matches(value: Any?): Boolean
}