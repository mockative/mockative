package io.mockative

class PetService(private val pets: PetStore, private val noises: NoiseStore) {
    fun add(pet: Pet) = pets.add(pet)

    fun pet(name: String): Pet = pets.pet(name)
    fun petOrNull(name: String): Pet? = pets.petOrNull(name)

    fun addNoise(name: String, play: () -> Unit) = noises.addNoise(name, play)
    fun playNoise(name: String) = noises.noise(name)()
}