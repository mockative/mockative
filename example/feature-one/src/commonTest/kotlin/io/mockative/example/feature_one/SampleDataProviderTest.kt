package io.mockative.example.feature_one

import kotlin.test.Test
import kotlin.test.assertEquals

class SampleDataProviderTest {

    val sampleDataProvider = mock(of<SampleDataProvider>())

    @Test
    fun testProvideById() {
        // Given
        val id = 1
        val expected = SampleData(id, "one", 10)
        every { sampleDataProvider.provide(id) }.returns(expected)

        // When
        val actual = sampleDataProvider.provide(id)

        // Then
        assertEquals(expected, actual)
    }

    @Test
    fun testProvideByValue() {
        // Given
        val value = "two"
        val expected = SampleData(2, value, 20)
        every { sampleDataProvider.provide(value) }.returns(expected)

        // When
        val actual = sampleDataProvider.provide(value)

        // Then
        assertEquals(expected, actual)
    }
}
