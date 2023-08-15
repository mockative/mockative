package io.mockative

val once: Int = 1
val twice: Int = 2

val Int.times: Int
    get() = this

val Int.time: Int
    get() = this

class Verification(private val receiver: Mockable, private val expectation: Expectation) {
    fun wasInvoked() {
        receiver.verify(RangeVerifier(expectation, atLeast = 1, atMost = null))
    }

    fun wasInvoked(atLeast: Int? = null, atMost: Int? = null) {
        receiver.verify(RangeVerifier(expectation, atLeast, atMost))
    }

    fun wasInvoked(exactly: Int) {
        receiver.verify(ExactVerifier(expectation, exactly))
    }

    fun wasNotInvoked() {
        receiver.verify(ExactVerifier(expectation, 0))
    }
}
