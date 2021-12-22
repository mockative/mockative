package io.mockative

class NoiseStoreMock : io.mockative.Mockable(stubsUnitByDefault = true), io.mockative.NoiseStore {
    override var noises: kotlin.collections.Map<kotlin.String, kotlin.Function0<kotlin.Unit>>
        get() = io.mockative.Mockable.invoke(this, io.mockative.Invocation.Getter("noises"), false)
        set(value) = io.mockative.Mockable.invoke(this, io.mockative.Invocation.Setter("noises", value), true)
    override val readOnlyNoises: kotlin.collections.Map<kotlin.String, kotlin.Function0<kotlin.Unit>>
        get() = io.mockative.Mockable.invoke(this, io.mockative.Invocation.Getter("readOnlyNoises"), false)

    override fun addNoise(name: kotlin.String, play: kotlin.Function0<kotlin.Unit>): kotlin.Unit = io.mockative.Mockable.invoke<kotlin.Unit>(this, io.mockative.Invocation.Function("addNoise", listOf<Any?>(name, play)), true)
    override fun clear(): kotlin.Unit = io.mockative.Mockable.invoke<kotlin.Unit>(this, io.mockative.Invocation.Function("clear", emptyList<Any?>()), true)
    override fun noise(name: kotlin.String): kotlin.Function0<kotlin.Unit> = io.mockative.Mockable.invoke<kotlin.Function0<kotlin.Unit>>(this, io.mockative.Invocation.Function("noise", listOf<Any?>(name)), false)
}