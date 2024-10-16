package io.github

import io.mockative.Mockable

sealed interface SealedInterface {
    val name: String
}

class SealedInterfaceImplementation(override val name: String) : SealedInterface

@Mockable
interface SealedInterfaceService {
    fun acceptSealedInterface(value: SealedInterface)
}
