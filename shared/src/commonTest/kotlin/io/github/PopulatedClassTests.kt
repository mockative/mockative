package io.github

import io.mockative.MissingExpectationException
import io.mockative.Mock
import io.mockative.classOf
import io.mockative.every
import io.mockative.isMock
import io.mockative.mock
import kotlin.test.Test
import kotlin.test.assertTrue

class PopulatedClassTests {
    @Mock
    val class2Mock = mock(classOf<Class2>())

    @Mock
    val mock: PopulatedClass = mock(classOf<PopulatedClass>())

    @Mock
    val class3Mock = mock(classOf<Class3>())

    @Test
    fun isMockEmptyClass() {
        assertTrue(isMock(mock))
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
        val exception = try {
            mock.inner
        } catch (e: Exception) {
            e
        }

        // Then
        assertTrue(exception is MissingExpectationException)
    }

    @Test
    fun givenExpectationOnMockedConstrcutorParameter_thenExpectationIsReturned() {
        // Given
        every { mock.class3 }.returns(class3Mock)
        every { class3Mock.message1 }.returns("something not message1 in class3")

        // When
        val getClass2FromMockResult = mock.class3
        val resultGetPropertyInClass2 = getClass2FromMockResult.message1

        // Then
        assertTrue(getClass2FromMockResult == class3Mock)
        assertTrue(resultGetPropertyInClass2 == "something not message1 in class3")
    }
}
