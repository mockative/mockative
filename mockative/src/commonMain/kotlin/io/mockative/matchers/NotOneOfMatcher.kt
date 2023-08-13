package io.mockative.matchers

class NotOneOfMatcher<T>(private val values: List<T>) : Matcher<T> {
    override val placeholder: T
        get() = values.first()

    override fun matches(value: Any?): Boolean {
        return value !in values
    }

    override fun toString(): String {
        return "<value !in (${values.joinToString(",")})>"
    }
}
