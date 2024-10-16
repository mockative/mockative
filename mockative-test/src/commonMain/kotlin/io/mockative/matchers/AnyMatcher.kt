package io.mockative.matchers

class AnyMatcher<T>(override val placeholder: T) : Matcher<T> {
    override fun matches(value: Any?): Boolean {
        return true
    }

    override fun toString(): String {
        return "<any>"
    }
}
