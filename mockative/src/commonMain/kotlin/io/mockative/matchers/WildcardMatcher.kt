package io.mockative.matchers

class WildcardMatcher<T> : Matcher<T> {
    override fun matches(value: Any?): Boolean {
        return true
    }

    override fun toString(): String {
        return "*"
    }
}