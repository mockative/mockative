package io.github

import io.mockative.Mock
import io.mockative.any
import io.mockative.classOf
import io.mockative.justRun
import io.mockative.mock
import io.mockative.once
import io.mockative.verify
import kotlin.test.Test

class SealedInterfaceServiceTests {
    @Mock
    val sis = mock(classOf<SealedInterfaceService>())

    @Test
    fun sealedInterfaceTest() {
        // Given
        justRun { sis.acceptSealedInterface(any()) }

        val value = SealedInterfaceImplementation("Mockative")

        // When
        sis.acceptSealedInterface(value)

        // Then
        verify(exactly = once) { sis.acceptSealedInterface(value) }
    }
}
