package io.mockative

class RangeVerifier(
    private val expectation: Expectation,
    private val atLeast: Int?,
    private val atMost: Int?
) : Verifier {
    override fun verify(instance: Any, invocations: List<Invocation>): List<Invocation> {
        val matchingInvocations = invocations.filter { expectation.matches(it) }

        val actual = matchingInvocations.size
        if (actual !in (atLeast ?: 0) until (atMost ?: 0) + 1) {
            throw RangeVerificationError(instance, atLeast, atMost, actual, expectation, invocations)
        }

        return matchingInvocations
    }
}