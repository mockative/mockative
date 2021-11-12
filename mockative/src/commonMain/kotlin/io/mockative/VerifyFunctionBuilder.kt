package io.mockative

import io.mockative.matchers.Matcher
import io.mockative.matchers.SpecificArgumentsMatcher

class VerifyFunctionBuilder(private val receiver: Mockable, private val function: String) {
    fun with(vararg arguments: Matcher<*>): Verification {
        val matcher = SpecificArgumentsMatcher(arguments.toList())
        return Verification(receiver, Expectation.Function(function, matcher))
    }
}