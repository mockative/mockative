package io.mockative

val once: Int = 1
val twice: Int = 2

val Int.times: Int
    get() = this

val Int.time: Int
    get() = this

class Verification(private val mock: MockState, private val expectation: Expectation) {
    fun wasInvoked() {
        mock.verify(RangeVerifier(expectation, atLeast = 1, atMost = null))
    }

    fun wasInvoked(atLeast: Int? = null, atMost: Int? = null) {
        mock.verify(RangeVerifier(expectation, atLeast, atMost))
    }

    fun wasInvoked(exactly: Int) {
        mock.verify(ExactVerifier(expectation, exactly))
    }

    fun wasNotInvoked() {
        mock.verify(ExactVerifier(expectation, 0))
    }
}
