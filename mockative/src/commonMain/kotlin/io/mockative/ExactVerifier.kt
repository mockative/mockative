package io.mockative

class ExactVerifier(
    private val expectation: Expectation,
    private val count: Int
) : Verifier {
    override fun verify(invocations: List<Invocation>): List<Invocation> {
        val matchingInvocations = invocations.filter { expectation.matches(it) }
        if (matchingInvocations.size != count) {
            TODO("ExactVerification expectation not met")
        }

        return matchingInvocations
    }
}