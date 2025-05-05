package io.mockative

internal class RangeVerifier(
    private val expectation: Expectation,
    private val atLeast: Int?,
    private val atMost: Int?
) : Verifier {
    override fun verify(instance: Any, invocations: List<Invocation>): List<Invocation> {
        val matchingInvocations = invocations.filter { expectation.matches(it) }

        val actual = matchingInvocations.size
        if (actual < (atLeast ?: 0) || atMost != null && actual > atMost) {
            throw RangeVerificationException(instance, atLeast, atMost, actual, expectation, invocations)
        }

        return matchingInvocations
    }
}
