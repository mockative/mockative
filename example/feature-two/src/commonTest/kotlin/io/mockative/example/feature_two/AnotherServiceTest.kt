package io.mockative.example.feature_two

import io.mockative.coEvery
import io.mockative.every
import io.mockative.mock
import io.mockative.of
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AnotherServiceTest {

    private val anotherService = mock(of<AnotherService>())

    @Test
    fun testDoSomething() {
        // Given
        val input = "hello"
        val expected = "world"
        every { anotherService.doSomething(input) }.returns(expected)

        // When
        val actual = anotherService.doSomething(input)

        // Then
        assertEquals(expected, actual)
    }

    @Test
    fun testDoSomethingAsync() = runTest {
        // Given
        val input = 42
        val expected = true
        coEvery { anotherService.doSomethingAsync(input) }.returns(expected)

        // When
        val actual = anotherService.doSomethingAsync(input)

        // Then
        assertEquals(expected, actual)
    }
}
