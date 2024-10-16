package io.mockative

internal interface Verifier {
    fun verify(instance: Any, invocations: List<Invocation>): List<Invocation>
}
