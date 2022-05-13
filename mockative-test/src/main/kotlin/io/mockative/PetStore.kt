package io.mockative

@MockableType
interface PetStore<T : CharSequence> {
    var pets: Map<String, () -> Unit>
    val readOnlyPets: Map<String, () -> Unit>

    fun add(pet: Pet)
    fun pet(name: String): Pet
    fun petOrNull(name: String): Pet?

    fun <P : Number> generic(type: T, pet: P): CharSequence

    fun clear()

    fun <R> call(function: Any.() -> R): R
}