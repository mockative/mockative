package io.mockative

class VerifyBuilder<T : Any>(private val receiver: T) {
    /**
     * Verifies whether an invocation on the receiver was performed at least and/or most a given
     * number of times.
     *
     * @param least the minimum number of invocations to expect
     * @param most the maximum number of invocations to expect
     */
    fun <R> at(least: Int? = null, most: Int? = null, block: suspend T.() -> R) {
        verify(block) { expectation -> RangeVerifier(expectation, least, most) }
    }

    /**
     * Verifies whether an invocation on the receiver was performed an exact number of times.
     *
     * @param count the number of invocations to expect
     */
    fun <R> exactly(count: Int, block: suspend T.() -> R) {
        verify(block) { expectation -> ExactVerifier(expectation, count) }
    }

    private inline fun <R> verify(
        noinline block: suspend T.() -> R,
        verifier: (Expectation) -> Verifier
    ) {
        val mock = receiver.asMockable()
        val invocation = mock.record(block)
        val expectation = invocation.toExpectation()
        mock.verify(verifier(expectation))
    }
}

/**
 * Verifies whether an invocation on the receiver was performed.
 */
fun <T : Any> verify(receiver: T): VerifyBuilder<T> {
    return VerifyBuilder(receiver)
}

/**
 * Verifies whether an invocation on the receiver was performed.
 */
fun <T : Any, R> verify(receiver: T, block: suspend T.() -> R) {
    verify(receiver).at(least = 1, block = block)
}

/**
 * Confirms whether all invocations were verified using [verify].
 */
fun <T : Any> confirmVerified(receiver: T) {
    val mock = receiver.asMockable()
    mock.confirmVerified()
}
