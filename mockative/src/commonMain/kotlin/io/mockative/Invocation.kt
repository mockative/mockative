package io.mockative

import io.mockative.matchers.AnyArgumentsMatcher
import io.mockative.matchers.SpecificArgumentsMatcher

sealed class Invocation {
    abstract fun toExpectation(): Expectation
    abstract fun toOpenExpectation(): Expectation

    class Function(val name: String, val arguments: List<Any?>) : Invocation() {
        override fun toExpectation(): Expectation.Function {
            return Expectation.Function(name, SpecificArgumentsMatcher(arguments.map { eq(it) }))
        }

        override fun toOpenExpectation(): Expectation {
            return Expectation.Function(name, AnyArgumentsMatcher)
        }

        override fun toString(): String {
            return "$name(${arguments.joinToString(", ")})"
        }
    }

    class Getter(val name: String) : Invocation() {
        override fun toExpectation(): Expectation.Getter {
            return Expectation.Getter(name)
        }

        override fun toOpenExpectation(): Expectation {
            return Expectation.Getter(name)
        }

        override fun toString(): String {
            return name
        }
    }

    class Setter(val name: String, val value: Any?) : Invocation() {
        override fun toExpectation(): Expectation {
            return Expectation.Setter(name, eq(value))
        }

        override fun toOpenExpectation(): Expectation {
            return Expectation.Setter(name, anything<Any?>())
        }

        override fun toString(): String {
            return "$name = $value"
        }
    }
}
