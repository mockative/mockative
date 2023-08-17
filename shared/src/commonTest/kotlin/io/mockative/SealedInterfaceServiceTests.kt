package io.mockative

import kotlin.test.Test

class SealedInterfaceServiceTests {
    @Mock
    val sis = mock(classOf<SealedInterfaceService>())

    @Test
    fun sealedInterfaceTest() {
        // Given
        every { sis.acceptSealedInterface(any()) }.doesNothing()

        val value = SealedInterfaceImplementation("Mockative")

        // When
        sis.acceptSealedInterface(value)

        // Then
        verify { sis.acceptSealedInterface(value) }.wasInvoked(exactly = once)
    }
}
