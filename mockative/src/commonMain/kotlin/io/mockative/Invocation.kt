package io.mockative

import io.mockative.matchers.AnyArgumentsMatcher
import io.mockative.matchers.AnyMatcher
import io.mockative.matchers.EqualsMatcher
import io.mockative.matchers.SpecificArgumentsMatcher

sealed class Invocation {
    abstract var result: InvocationResult?

    abstract fun toExpectation(): Expectation
    abstract fun toOpenExpectation(): Expectation

    class Function(val name: String, val arguments: List<Any?>) : Invocation() {
        override var result: InvocationResult? = null

        override fun toExpectation(): Expectation.Function {
            if (Matchers.size == 0) {
                val arguments = arguments
                for (index in arguments.indices) {
                    val argument = arguments[index]
                    Matchers.enqueue(EqualsMatcher(argument))
                }
            }

            if (arguments.size != Matchers.size) {
                throw MixedArgumentMatcherException()
            }

            val matchers = arguments.map { Matchers.dequeue() }
            return Expectation.Function(name, SpecificArgumentsMatcher(matchers))
        }

        override fun toOpenExpectation(): Expectation {
            return Expectation.Function(name, AnyArgumentsMatcher)
        }

        override fun toString(): String {
            return buildString {
                append("$name(${arguments.joinToString(", ")})")

                if (result != null) {
                    appendLine(" = ")
                    append("    $result")
                }
            }
        }
    }

    class Getter(val name: String) : Invocation() {
        override var result: InvocationResult? = null

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
        override var result: InvocationResult? = null

        override fun toExpectation(): Expectation {
            val matcher = if (Matchers.size > 0) Matchers.dequeue() else EqualsMatcher(value)
            return Expectation.Setter(name, matcher)
        }

        override fun toOpenExpectation(): Expectation {
            return Expectation.Setter(name, AnyMatcher<Any?>(null))
        }

        override fun toString(): String {
            return "$name = $value"
        }
    }
}
