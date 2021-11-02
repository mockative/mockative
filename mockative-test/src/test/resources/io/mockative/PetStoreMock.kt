package io.mockative

class PetStoreMock : io.mockative.Mocked<io.mockative.PetStore>(), io.mockative.PetStore {
    override var pets: kotlin.collections.Map<kotlin.String, kotlin.Function0<kotlin.Unit>>
        get() = mockGetter("pets")
        set(value) = mockSetter("pets", value)
    override val readOnlyPets: kotlin.collections.Map<kotlin.String, kotlin.Function0<kotlin.Unit>>
        get() = mockGetter("readOnlyPets")

    override fun add(pet: io.mockative.Pet): kotlin.Unit = mock<kotlin.Unit>("add", pet)
    override fun clear(): kotlin.Unit = mock<kotlin.Unit>("clear")
    override fun pet(name: kotlin.String): io.mockative.Pet = mock<io.mockative.Pet>("pet", name)
    override fun petOrNull(name: kotlin.String): io.mockative.Pet? = mock<io.mockative.Pet?>("petOrNull", name)
}