package io.mockative

class VerifyBuilder<T : Any>(private val receiver: T) {
    val coroutine: SuspendVerifyBuilder<T>
        get() = SuspendVerifyBuilder(receiver)

    /**
     * Verifies whether an invocation on the receiver was performed at least and/or most a given
     * number of times.
     *
     * @param least the minimum number of invocations to expect
     * @param most the maximum number of invocations to expect
     */
    fun <R> at(least: Int? = null, most: Int? = null, block: T.() -> R) {
        verify(block) { expectation -> RangeVerifier(expectation, least, most) }
    }

    /**
     * Verifies whether an invocation on the receiver was performed an exact number of times.
     *
     * @param count the number of invocations to expect
     */
    fun <R> exactly(count: Int, block: T.() -> R) {
        verify(block) { expectation -> ExactVerifier(expectation, count) }
    }

    private fun <R> verify(block: T.() -> R, verifier: (Expectation) -> Verifier) {
        val mock = receiver.asMockable()
        val invocation = mock.record(block)
        val expectation = invocation.toExpectation()
        mock.verify(verifier(expectation))
    }
}
