package io.mockative

interface Verifier {
    fun verify(instance: Any, invocations: List<Invocation>): List<Invocation>
}
