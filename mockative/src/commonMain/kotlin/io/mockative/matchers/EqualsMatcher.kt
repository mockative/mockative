package io.mockative.matchers

class EqualsMatcher<T>(private val expected: T) : Matcher<T> {
    override val placeholder: T
        get() = expected

    override fun matches(value: Any?): Boolean {
        return expected == value
    }

    override fun toString(): String {
        return expected.toString()
    }
}
