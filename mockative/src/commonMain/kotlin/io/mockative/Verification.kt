package io.mockative

val once: Int = 1
val twice: Int = 2

val Int.times: Int
    get() = this

val Int.time: Int
    get() = this

class Verification(private val mock: io.mockative.MockState, private val expectation: io.mockative.Expectation) {
    fun wasInvoked() {
        mock.verify(io.mockative.RangeVerifier(expectation, atLeast = 1, atMost = null))
    }

    fun wasInvoked(atLeast: Int? = null, atMost: Int? = null) {
        mock.verify(io.mockative.RangeVerifier(expectation, atLeast, atMost))
    }

    fun wasInvoked(exactly: Int) {
        mock.verify(io.mockative.ExactVerifier(expectation, exactly))
    }

    fun wasNotInvoked() {
        mock.verify(io.mockative.ExactVerifier(expectation, 0))
    }
}
