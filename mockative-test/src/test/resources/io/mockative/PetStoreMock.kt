package io.mockative

class PetStoreMock : io.mockative.Mockable(), io.mockative.PetStore {
    override var pets: kotlin.collections.Map<kotlin.String, kotlin.Function0<kotlin.Unit>>
        get() = io.mockative.Mockable.invoke(this, io.mockative.Invocation.Getter("pets"))
        set(value) = io.mockative.Mockable.invoke(this, io.mockative.Invocation.Setter("pets", value))
    override val readOnlyPets: kotlin.collections.Map<kotlin.String, kotlin.Function0<kotlin.Unit>>
        get() = io.mockative.Mockable.invoke(this, io.mockative.Invocation.Getter("readOnlyPets"))

    override fun add(pet: io.mockative.Pet): kotlin.Unit = io.mockative.Mockable.invoke<kotlin.Unit>(this, io.mockative.Invocation.Function("add", listOf<Any?>(pet)))
    override fun clear(): kotlin.Unit = io.mockative.Mockable.invoke<kotlin.Unit>(this, io.mockative.Invocation.Function("clear", emptyList<Any?>()))
    override fun pet(name: kotlin.String): io.mockative.Pet = io.mockative.Mockable.invoke<io.mockative.Pet>(this, io.mockative.Invocation.Function("pet", listOf<Any?>(name)))
    override fun petOrNull(name: kotlin.String): io.mockative.Pet? = io.mockative.Mockable.invoke<io.mockative.Pet?>(this, io.mockative.Invocation.Function("petOrNull", listOf<Any?>(name)))
}