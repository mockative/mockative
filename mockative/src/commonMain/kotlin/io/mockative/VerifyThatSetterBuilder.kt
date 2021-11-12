package io.mockative

import io.mockative.matchers.Matcher

class VerifyThatSetterBuilder<V>(private val receiver: Mockable, private val property: String) {
    fun with(value: Matcher<V>): Verification {
        return Verification(receiver, Expectation.Setter(property, value))
    }
}