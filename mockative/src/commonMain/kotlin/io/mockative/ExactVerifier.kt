package io.mockative

internal class ExactVerifier(
    private val expectation: Expectation,
    private val count: Int
) : Verifier {
    override fun verify(instance: Any, invocations: List<Invocation>): List<Invocation> {
        val matchingInvocations = invocations.filter { expectation.matches(it) }

        val actual = matchingInvocations.size
        if (actual != count) {
            throw ExactVerificationException(instance, count, actual, expectation, invocations)
        }

        return matchingInvocations
    }
}
