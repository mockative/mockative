package io.mockative

import kotlin.reflect.KClass

abstract class MockativeException(override val message: String) : Exception(message)

class ValueCreationNotSupportedException(type: KClass<*>) : MockativeException(
    buildString {
        appendLine("Cannot create a placeholder value of type '$type'")
        appendLine()
        appendLine(1, "Placeholder values (and thus some matchers) are not supported when targeting Kotlin/Wasm")
    }
)

class NoSuchMockException(type: KClass<*>) : MockativeException(
    buildString {
        appendLine("A mock for the type ${type.name} was not generated.")
        appendLine()
        appendLine(1, "Make sure the property holding the mock is annotated with @Mock:")
        appendLine()
        appendLine(2, "@Mock")
        appendLine(2, "private val myMock = mock(of<${type.name}>())")
        appendLine(1, "")
    }
)

class ReceiverNotMockedException(receiver: Any) : MockativeException(
    buildString {
        appendLine("Attempt to perform operation a non-mock instance of type ${receiver.getClassName()}.")
        appendLine()
        appendLine(1, "Make sure the property holding the mock is annotated with @Mock:")
        appendLine()
        appendLine(2, "@Mock")
        appendLine(2, "private val myMock = mock(of<${receiver.getClassName()}>())")
        appendLine(1, "")
    }
)

class ExactVerificationException(
    instance: Any,
    expected: Int,
    actual: Int,
    expectation: Expectation,
    invocations: List<Invocation>
) : MockativeException(
    buildString {
        appendLine("A mock of type ${instance.getClassName()} was not invoked the expected number of times.")
        appendLine()
        appendLine(1, "Expected $expected invocations of $expectation")
        appendLine(1, "Actual: $actual")
        appendLine()

        if (invocations.isEmpty()) {
            appendLine(2, "No invocation on the mock were recorded.")
        } else {
            invocations.forEach { invocation ->
                appendLine(2, "$invocation")
            }
        }

        appendLine(1, "")
    }
)

class RangeVerificationException(
    instance: Any,
    atLeast: Int?,
    atMost: Int?,
    actual: Int,
    expectation: Expectation,
    invocations: List<Invocation>
) : MockativeException(
    buildString {
        appendLine("A mock of type ${instance.getClassName()} was not invoked the expected number of times.")
        appendLine()

        val expected = when {
            atLeast != null && atMost != null -> "at least $atLeast and at most $atMost "
            atLeast != null -> "at least $atLeast "
            atMost != null -> "at most $atMost "
            else -> "at least 1"
        }

        appendLine(1, "Expected ${expected}invocations of $expectation")
        appendLine(1, "Actual: $actual")
        appendLine()

        if (invocations.isEmpty()) {
            appendLine(2, "No invocation on the mock were recorded.")
        } else {
            invocations.forEach { invocation ->
                appendLine(2, "$invocation")
            }
        }

        appendLine(1, "")
    }
)

class UnverifiedInvocationsException(instance: Any, invocations: List<Invocation>) : MockativeException(
    buildString {
        appendLine(0, "A mock contains unverified invocations.")
        appendLine()
        appendLine(1, "The following invocations on the type ${instance.getClassName()} were not verified:")
        appendLine()

        invocations.forEach { invocation ->
            appendLine(2, "$invocation")
        }

        appendLine(1, "")
    }
)

class MockValidationException(instance: Any, expectations: List<Expectation>, invocations: List<Invocation>) :
    MockativeException(
        buildString {
            appendLine("Validation of mock failed.")
            appendLine()
            appendLine(1, "The following expectations on the type ${instance.getClassName()} were not met.")
            appendLine()

            expectations.forEach { expectation ->
                appendLine(2, "$expectation")
            }

            appendLine()

            appendLine(1, "The following invocations were recorded:")
            appendLine()

            invocations.forEach { invocation ->
                appendLine(2, "$invocation")
            }

            appendLine(1, "")
        }
    )

class MissingExpectationException(
    instance: Any,
    invocation: Invocation,
    isSuspend: Boolean,
    expectations: List<Expectation>
) : MockativeException(
    buildString {
        appendLine("A function was called without a matching expectation.")
        appendLine()
        appendLine(1, "An expectation was not given on the function:")
        appendLine(2, "${instance.getClassName()}.$invocation")
        appendLine()
        appendLine(1, "Set up an expectation using:")

        val every = if (isSuspend) "coEvery" else "every"
        val propertyName = instance.getPropertyName()
        appendLine(2, "$every { $propertyName.$invocation }")
        appendLine(3, ".invokes { ... }")
        appendLine(1, "")
        appendLine(1, "The following expectations were configured on the mock:")
        expectations.forEach {
            appendLine(2, "$it")
        }
        appendLine(1, "")
    }
)

class InvalidExpectationException(
    instance: Any,
    invocation: Invocation,
    isSuspend: Boolean,
    expectations: List<Expectation>
) : MockativeException(
    buildString {
        appendLine("A function was called without a matching expectation.")
        appendLine()
        
        val expectedType = if (isSuspend) "blocking" else "coroutine"
        val actualType = if (isSuspend) "coroutine" else "blocking"
        appendLine(1, "A $expectedType stub was expected, but a $actualType stub was configured on the function:")
        appendLine(2, "${instance.getClassName()}.$invocation")
        appendLine()
        appendLine(1, "Set up an expectation using:")

        val every = if (isSuspend) "coEvery" else "every"
        val propertyName = instance.getPropertyName()
        appendLine(2, "$every { $propertyName.$invocation }")
        appendLine(3, ".invokes { ... }")
        appendLine(1, "")
        appendLine(1, "The following expectations were configured on the mock:")
        expectations.forEach {
            appendLine(2, "$it")
        }
        appendLine(1, "")
    }
)

class MixedArgumentMatcherException :
    MockativeException("Mixing values and matchers like `eq()` or `any()` in the same function call is not supported.")

class StubbingInProgressException(val receiver: MockState, val invocation: Invocation) : Exception()

private inline fun buildString(block: Appendable.() -> Unit): String {
    return StringBuilder().also { block(it) }.toString()
}

private fun Appendable.appendIndentation(level: Int) {
    for (i in 0 until level) {
        append("    ")
    }
}

private fun Appendable.appendLine(indentation: Int, value: String) {
    appendIndentation(indentation)
    appendLine(value)
}
