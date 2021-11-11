package io.mockative

import io.mockative.matchers.SpecificArgumentsMatcher

sealed class Invocation {
    abstract fun toExpectation(): Expectation

    data class Function(val name: String, val arguments: List<Any?>) : Invocation() {
        override fun toExpectation(): Expectation.Function {
            return Expectation.Function(name, SpecificArgumentsMatcher(arguments.map { eq(it) }))
        }

        override fun toString(): String {
            return "$name(${arguments.joinToString(", ")})"
        }
    }

    data class Getter(val name: String) : Invocation() {
        override fun toExpectation(): Expectation.Getter {
            return Expectation.Getter(name)
        }

        override fun toString(): String {
            return name
        }
    }

    data class Setter(val name: String, val value: Any?) : Invocation() {
        override fun toExpectation(): Expectation {
            return Expectation.Setter(name, eq(value))
        }

        override fun toString(): String {
            return "$name = $value"
        }
    }
}
