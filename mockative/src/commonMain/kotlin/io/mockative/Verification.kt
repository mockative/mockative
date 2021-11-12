package io.mockative

inline val Int.times: Int
    get() = this

inline val Int.time: Int
    get() = this

class Verification(private val receiver: Mockable, private val expectation: Expectation) {
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