package io.mockative

val once: Int = 1
val twice: Int = 2

val Int.times: Int
    get() = this

val Int.time: Int
    get() = this

class Verification(private val mockable: Mockable, private val expectation: Expectation) {
    fun wasInvoked() {
        mockable.verify(RangeVerifier(expectation, atLeast = 1, atMost = null))
    }

    fun wasInvoked(atLeast: Int? = null, atMost: Int? = null) {
        mockable.verify(RangeVerifier(expectation, atLeast, atMost))
    }

    fun wasInvoked(exactly: Int) {
        mockable.verify(ExactVerifier(expectation, exactly))
    }

    fun wasNotInvoked() {
        mockable.verify(ExactVerifier(expectation, 0))
    }
}
