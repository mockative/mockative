package io.mockative

internal fun mock(@Suppress("UNUSED_PARAMETER") type: kotlin.reflect.KClass<io.mockative.PetStore>): io.mockative.PetStore = io.mockative.PetStoreMock()
internal fun mock(@Suppress("UNUSED_PARAMETER") type: kotlin.reflect.KClass<io.mockative.NoiseStore>): io.mockative.NoiseStore = io.mockative.NoiseStoreMock()
