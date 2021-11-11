package io.mockative

interface Verifier {
    fun verify(invocations: List<Invocation>): List<Invocation>
}
