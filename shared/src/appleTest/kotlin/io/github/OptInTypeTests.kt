package io.github

import io.mockative.of
import io.mockative.doesNothing
import io.mockative.every
import io.mockative.mock
import kotlinx.cinterop.ExperimentalForeignApi
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalForeignApi::class)
class OptInTypeTests {
    val sut = mock(of<OptInType>())

    @Test
    fun test() {
        // Given
        every { sut.foo(null) }
            .doesNothing()

        // When
        val result = sut.foo(null)

        // Then
        assertEquals(Unit, result)
    }
}
