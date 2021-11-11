package io.mockative

class RangeVerifier(
    private val expectation: Expectation,
    private val min: Int?,
    private val max: Int?
) : Verifier {
    override fun verify(invocations: List<Invocation>): List<Invocation> {
        val matchingInvocations = invocations.filter { expectation.matches(it) }
        if (matchingInvocations.size !in (min ?: 0) until (max ?: 0) + 1) {
            TODO("RangeVerification expectation not met")
        }

        return matchingInvocations
    }
}