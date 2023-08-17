package io.mockative

sealed interface SealedInterface {
    val name: String
}

class SealedInterfaceImplementation(override val name: String) : SealedInterface

interface SealedInterfaceService {
    fun acceptSealedInterface(value: SealedInterface)
}
