package io.github

import io.mockative.any
import io.mockative.classOf
import io.mockative.every
import io.mockative.mock
import io.mockative.once
import io.mockative.verify
import kotlin.test.Test

class SealedInterfaceServiceTests {
    val sis = mock(classOf<SealedInterfaceService>())

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
