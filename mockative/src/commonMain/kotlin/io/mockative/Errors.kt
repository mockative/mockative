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

class StubbingNonMockError(receiver: Any) : Error(
    """
        Attempt to stub a non-mock instance of type ${receiver.getClassName()}.
        
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

class VerifyNonMockError(receiver: Any) : Error(
    """
        Attempt to verify a non-mock instance of type ${receiver.getClassName()}.
        
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

class ExpectationNotMetError(instance: Any, invocation: Invocation) : AssertionError(
    """
        Expectation not met of invocation on function.
        
            An expectation was given on the function:
                ${instance.getClassName()}.$invocation
                
            But no invocations of the function were recorded.
    """.trimIndent()
)

class MissingExpectationError(instance: Any, invocation: Invocation) : AssertionError(
    """
        A function was called without a matching expectation.
        
            An expectation was not given on the function:
                ${instance.getClassName()}.$invocation
                
            Set up an expectation using:
                given(instance) { $invocation }
                    .then { ... }
    """.trimIndent()
)

class SuspendedExpectationOnNonSuspendingFunctionError(instance: Any, invocation: Invocation) : AssertionError(
    """
        Suspended expectation configured on a non-suspending function.
        
            A suspended expectation was given on the function:
                ${instance.getClassName()}.$invocation
            
            Make sure to use one of the non-suspending expectations on non-suspending functions, e.g.:
                given(instance) { $invocation }
                    .then { ... }
    """.trimIndent()
)