package io.github

import io.mockative.Mock
import io.mockative.classOf
import io.mockative.doesNothing
import io.mockative.every
import io.mockative.mock
import kotlinx.cinterop.ExperimentalForeignApi
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalForeignApi::class)
class OptInTypeTests {
    @Mock
    val sut = mock(classOf<OptInType>())

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
