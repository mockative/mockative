package io.mockative

import org.junit.Test

class PetServiceTests {

    @Mock val petStore = mock(PetStore::class)
    @Mock val noiseStore = mock(NoiseStore::class)

    @Test
    fun generatesExpectedPetStoreMock() {
        assertGenerated("PetStoreMock.kt")
    }

    @Test
    fun generatesExpectedNoiseStoreMock() {
        assertGenerated("NoiseStoreMock.kt")
    }

    @Test
    fun generatesExpectedMockFunctions() {
        assertGenerated("Mocks.kt")
    }

}