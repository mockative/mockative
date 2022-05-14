package io.mockative

class PetStoreMock<T> : io.mockative.Mockable(stubsUnitByDefault = true), io.mockative.PetStore<T> where T : kotlin.CharSequence {
    override var pets: kotlin.collections.Map<kotlin.String, kotlin.Function0<kotlin.Unit>>
        get() = io.mockative.Mockable.invoke(this, io.mockative.Invocation.Getter("pets"), false)
        set(value) = io.mockative.Mockable.invoke(this, io.mockative.Invocation.Setter("pets", value), true)
    override val readOnlyPets: kotlin.collections.Map<kotlin.String, kotlin.Function0<kotlin.Unit>>
        get() = io.mockative.Mockable.invoke(this, io.mockative.Invocation.Getter("readOnlyPets"), false)

    override fun add(pet: io.mockative.Pet): kotlin.Unit = io.mockative.Mockable.invoke<kotlin.Unit>(this, io.mockative.Invocation.Function("add", listOf<Any?>(pet)), true)
    override fun <R> call(function: kotlin.Function1<kotlin.Any, R>): R where R : kotlin.Any? = io.mockative.Mockable.invoke<R>(this, io.mockative.Invocation.Function("call", listOf<Any?>(function)), false)
    override fun clear(): kotlin.Unit = io.mockative.Mockable.invoke<kotlin.Unit>(this, io.mockative.Invocation.Function("clear", emptyList<Any?>()), true)
    override fun <P> generic(type: T, pet: P): kotlin.CharSequence where P : kotlin.Number = io.mockative.Mockable.invoke<kotlin.CharSequence>(this, io.mockative.Invocation.Function("generic", listOf<Any?>(type, pet)), false)
    override fun pet(name: kotlin.String): io.mockative.Pet = io.mockative.Mockable.invoke<io.mockative.Pet>(this, io.mockative.Invocation.Function("pet", listOf<Any?>(name)), false)
    override fun petOrNull(name: kotlin.String): io.mockative.Pet? = io.mockative.Mockable.invoke<io.mockative.Pet?>(this, io.mockative.Invocation.Function("petOrNull", listOf<Any?>(name)), false)
}