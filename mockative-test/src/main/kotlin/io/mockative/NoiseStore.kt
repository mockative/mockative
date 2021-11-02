package io.mockative

interface NoiseStore {
    var noises: Map<String, () -> Unit>
    val readOnlyNoises: Map<String, () -> Unit>

    fun noise(name: String): () -> Unit
    fun addNoise(name: String, play: () -> Unit)

    fun clear()
}