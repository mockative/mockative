package io.mockative

internal fun mock(@Suppress("UNUSED_PARAMETER") type: kotlin.reflect.KClass<io.mockative.PetStore<*>>): io.mockative.PetStore<kotlin.CharSequence> = io.mockative.PetStoreMock<kotlin.CharSequence>()
internal fun mock(@Suppress("UNUSED_PARAMETER") type: kotlin.reflect.KClass<io.mockative.NoiseStore>): io.mockative.NoiseStore = io.mockative.NoiseStoreMock()
