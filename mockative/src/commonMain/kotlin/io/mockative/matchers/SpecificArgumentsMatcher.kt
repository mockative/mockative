package io.mockative.matchers

class SpecificArgumentsMatcher(private val matchers: List<Matcher<*>>) : ArgumentsMatcher {
    override fun matches(arguments: List<Any?>): Boolean {
        return matchers.size == arguments.size
                && matchers.zip(arguments).all { (matcher, argument) -> matcher.matches(argument) }
    }

    override fun toString(): String {
        return matchers.joinToString(", ")
    }
}