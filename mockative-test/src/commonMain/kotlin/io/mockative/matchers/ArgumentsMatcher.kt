package io.mockative.matchers

interface ArgumentsMatcher {
    fun matches(arguments: List<Any?>): Boolean
}