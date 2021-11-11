package io.mockative

import kotlin.reflect.KClass

class NoSuchMockError(type: KClass<*>) : Error(
    """
        A mock for the type ${type.name} was not generated.
        
            Make sure either the property holding the mock is annotated with @Mock, or a method or class is annotated with @Mocks:
                @Mock
                private val myMock = mock(${type.name}::class)
                
                ----- or -----
                
                private lateinit var myMock: ${type.name}
                
                @Mocks
                @BeforeTest
                fun setupMocks() {
                    myMock = mock(${type.name}::class)
                }
    """.trimIndent()
)

class ReceiverNotMockedError(receiver: Any) : Error(
    """
        Attempt to perform operation a non-mock instance of type ${receiver.getClassName()}.
        
            Make sure either the property holding the mock is annotated with @Mock, or a method or class is annotated with @Mocks:
                @Mock
                private val myMock = mock(${receiver.getClassName()}::class)
                
                ----- or -----
                
                private lateinit var myMock: ${receiver.getClassName()}
                
                @Mocks
                @BeforeTest
                fun setupMocks() {
                    myMock = mock(${receiver.getClassName()}::class)
                }
    """.trimIndent()
)

class ExactVerificationError(
    instance: Any,
    expected: Int,
    actual: Int,
    expectation: Expectation,
    invocations: List<Invocation>
) : AssertionError(
    StringBuilder()
        .run {
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

            appendLine()
        }
        .toString()
)

class RangeVerificationError(
    instance: Any,
    atLeast: Int?,
    atMost: Int?,
    actual: Int,
    expectation: Expectation,
    invocations: List<Invocation>
) : AssertionError(
    StringBuilder()
        .run {
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

            appendLine()
        }
        .toString()
)

fun StringBuilder.appendIndentation(level: Int) {
    for (i in 0 until level) {
        append(' ')
    }
}

fun StringBuilder.appendLine(indentation: Int, value: String) {
    appendIndentation(indentation)
    appendLine(value)
}

class UnverifiedInvocationsError(instance: Any, invocations: List<Invocation>) : AssertionError(
    StringBuilder()
        .run {
            appendLine(0, "A mock contains unverified invocations.")
            appendLine()
            appendLine(
                1,
                "The following invocations on the type ${instance.getClassName()} were not verified:"
            )
            appendLine()

            invocations.forEach { invocation ->
                appendLine(2, "$invocation")
            }

            appendLine()
        }
        .toString()
)

class MockValidationError(instance: Any, expectations: List<Expectation>) : AssertionError(
    StringBuilder()
        .run {
            appendLine("Validation of mock failed.")
            appendLine()
            appendLine(1, "The following expectations on the type ${instance.getClassName()} were not met.")
            appendLine()

            expectations.forEach { expectation ->
                appendLine(2, "$expectation")
            }
        }
        .toString()
)

class MissingExpectationError(instance: Any, invocation: Invocation, isSuspend: Boolean) : AssertionError(
    """
        A function was called without a matching expectation.
        
            An expectation was not given on the function:
                ${instance.getClassName()}.$invocation
                
            Set up an expectation using:
                given(instance)${if (isSuspend) ".coroutine" else ""} { $invocation }
                    .then { ... }
    """.trimIndent()
)
