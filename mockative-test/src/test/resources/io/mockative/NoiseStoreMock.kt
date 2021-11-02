package io.mockative

class NoiseStoreMock : io.mockative.Mocked<io.mockative.NoiseStore>(), io.mockative.NoiseStore {
    override var noises: kotlin.collections.Map<kotlin.String, kotlin.Function0<kotlin.Unit>>
        get() = mockGetter("noises")
        set(value) = mockSetter("noises", value)
    override val readOnlyNoises: kotlin.collections.Map<kotlin.String, kotlin.Function0<kotlin.Unit>>
        get() = mockGetter("readOnlyNoises")

    override fun addNoise(name: kotlin.String, play: kotlin.Function0<kotlin.Unit>): kotlin.Unit = mock<kotlin.Unit>("addNoise", name, play)
    override fun clear(): kotlin.Unit = mock<kotlin.Unit>("clear")
    override fun noise(name: kotlin.String): kotlin.Function0<kotlin.Unit> = mock<kotlin.Function0<kotlin.Unit>>("noise", name)
}