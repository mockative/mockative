package io.mockative

import org.junit.Test

@Mocks(PetStore::class)
class PetServiceTests {

    @Mock private val noiseStore = mock(NoiseStore::class)

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