package io.github

import io.mockative.any
import io.mockative.of
import io.mockative.every
import io.mockative.mock
import io.mockative.once
import io.mockative.verify
import kotlin.test.Test

class SealedInterfaceServiceTests {
    val sis = mock(of<SealedInterfaceService>())

    @Test
    fun sealedInterfaceTest() = ignoreKotlinWasm {
        // Given
        every { sis.acceptSealedInterface(any()) }
            .returnsMany()

        val value = SealedInterfaceImplementation("Mockative")

        // When
        sis.acceptSealedInterface(value)

        // Then
        verify { sis.acceptSealedInterface(value) }.wasInvoked(exactly = once)
    }
}
