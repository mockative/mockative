package io.mockative

import kotlin.test.Test

class PetServiceTests {

    @Mock val petStore = mock(classOf<PetStore<String>>())
    @Mock val noiseStore = mock(classOf<NoiseStore>())

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
        assertGenerated("GeneratedMocks.kt")
    }

}