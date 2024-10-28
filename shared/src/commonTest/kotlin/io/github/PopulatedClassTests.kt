package io.github

import io.mockative.*
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class PopulatedClassTests {
    val mock: PopulatedClass = mock(classOf<PopulatedClass>())
    val class3Mock = mock(classOf<Class3>())

    @Test
    fun isMockEmptyClass() {
        assertTrue(isMock(mock))
    }

    @Test
    fun givenEqValuesForFunctionWithReturnValue_whenCalledWithExactValues_ThenValueIsReturned() {
        // Given
        every { mock.withParameters("Hello", 1) }.returns("This is not Hello Hello 1")

        // When
        val result = mock.withParameters("Hello", 1)

        // Then
        assertTrue(result == "This is not Hello Hello 1")
    }

    @Test
    fun givenEqValuesForFunctionWithReturnValue_whenCalledWithDifferentValues_ThenMissingExpectationExceptionIsThrown() {
        // Given
        every { mock.withParameters("Hello", 1) }.returns("This is not Hello Hello 1")

        // When / then
        val result = kotlin.runCatching { mock.withParameters("Hello", 2) }
        assertTrue(result.exceptionOrNull() is MissingExpectationException)
    }

    @Test
    fun givenMatchersForFunctionWithReturnValue_whenCalledWithMatchingValues_ThenValueIsReturned() {
        // Given
        every { mock.withParameters(any("TODO"), gt(4)) }.returns("This is not Hello Hello 1")

        // When
        val result = mock.withParameters("Hello", 10)

        // Then
        assertTrue(result == "This is not Hello Hello 1")
    }

    @Test
    fun givenMatchersForFunctionWithReturnValue_whenCalledWithNonMatchingValues_ThenMissingExpectationExceptionIsThrown() {
        // Given
        every { mock.withParameters(any("TODO"), gt(4)) }.returns("This is not Hello Hello 1")

        // When / then
        val result = kotlin.runCatching { mock.withParameters("Hello", 1) }
        assertTrue(result.exceptionOrNull() is MissingExpectationException)
    }

    @Test
    fun testSetter() {
        // Given
        mock.charSequence = "Hello"

        // when / Then
        verify{ mock.charSequence = "Hello" }.wasInvoked(exactly = 1)
    }

    @Test
    fun givenExpectationSetup_thenExpectationIsReturned() {
        // Given
        every { mock.greet() }.returns("This is not hello")

        // When
        val result = mock.greet()

        // Then
        assertTrue(result == "This is not hello")
    }

    @Test
    fun givenNoExpectationOnMockedConstrcutorParameter_thenMissingExpectationIsThrown() {
        // Given / when
        val result = runCatching { mock.inner }

        // Then
        val exception = result.exceptionOrNull()
        assertIs<MissingExpectationException>(exception)
    }

//    @Test
//    fun givenExpectationOnMockedConstrcutorParameter_thenExpectationIsReturned() {
//        // Given
//        every { mock.class3 }.returns(class3Mock)
//        every { class3Mock.message1 }.returns("something not message1 in class3")
//
//        // When
//        val getClass2FromMockResult = mock.class3
//        val resultGetPropertyInClass2 = getClass2FromMockResult.message1
//
//        // Then
//        assertTrue(getClass2FromMockResult == class3Mock)
//        assertTrue(resultGetPropertyInClass2 == "something not message1 in class3")
//    }
}
