package io.mockative

val never: Int = 0
val once: Int = 1

internal class Verification(private val mockable: Mockable, private val expectation: Expectation) {
    fun wasInvoked() {
        mockable.verify(RangeVerifier(expectation, atLeast = 1, atMost = null))
    }

    fun wasInvoked(atLeast: Int?, atMost: Int?) {
        mockable.verify(RangeVerifier(expectation, atLeast, atMost))
    }

    fun wasInvoked(exactly: Int) {
        mockable.verify(ExactVerifier(expectation, exactly))
    }
}
