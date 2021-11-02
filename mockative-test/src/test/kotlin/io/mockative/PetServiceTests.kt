package io.mockative

import org.junit.Test

@Mocks(PetStore::class)
@Mocks(NoiseStore::class)
class PetServiceTests {

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