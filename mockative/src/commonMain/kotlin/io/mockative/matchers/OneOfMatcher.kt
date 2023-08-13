package io.mockative.matchers

class OneOfMatcher<T>(private val values: List<T>) : Matcher<T> {
    override val placeholder: T
        get() = values.first()

    override fun matches(value: Any?): Boolean {
        return values.contains(value)
    }

    override fun toString(): String {
        return "oneOf(${values.joinToString(",")})"
    }
}
