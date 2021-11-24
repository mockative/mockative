package io.mockative

internal fun <T> mock(@Suppress("UNUSED_PARAMETER") type: kotlin.reflect.KClass<io.mockative.PetStore<T>>): io.mockative.PetStore<T> where T : kotlin.CharSequence = io.mockative.PetStoreMock<T>()
internal fun mock(@Suppress("UNUSED_PARAMETER") type: kotlin.reflect.KClass<io.mockative.NoiseStore>): io.mockative.NoiseStore = io.mockative.NoiseStoreMock()
