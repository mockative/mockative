package io.mockative.matchers

object AnyArgumentsMatcher : ArgumentsMatcher {
    override fun matches(arguments: List<Any?>): Boolean {
        return true
    }

    override fun toString(): String {
        return "..."
    }
}