package io.mockative

data class PetAlreadyExistsError(val pet: Pet) : Error()