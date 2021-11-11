package io.mockative

import io.mockative.matchers.ArgumentsMatcher
import io.mockative.matchers.Matcher

sealed class Expectation {

    abstract fun matches(invocation: Invocation): Boolean

    data class Function(val name: String, val arguments: ArgumentsMatcher) : Expectation() {
        override fun matches(invocation: Invocation): Boolean {
            return invocation is Invocation.Function && matches(invocation)
        }

        private fun matches(invocation: Invocation.Function): Boolean {
            return name == invocation.name && arguments.matches(invocation.arguments)
        }

        override fun toString(): String {
            return "$name(${arguments})"
        }
    }

    data class Getter(val name: String) : Expectation() {
        override fun matches(invocation: Invocation): Boolean {
            return invocation is Invocation.Getter && matches(invocation)
        }

        private fun matches(invocation: Invocation.Getter): Boolean {
            return name == invocation.name
        }

        override fun toString(): String {
            return name
        }
    }

    data class Setter(val name: String, val value: Matcher<*>) : Expectation() {
        override fun matches(invocation: Invocation): Boolean {
            return invocation is Invocation.Setter && matches(invocation)
        }

        private fun matches(invocation: Invocation.Setter): Boolean {
            return name == invocation.name && value.matches(invocation.value)
        }

        override fun toString(): String {
            return "$name = $value"
        }
    }
}