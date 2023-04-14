package io.mockative

import kotlin.jvm.JvmInline

val once: Times = Times(1)
val twice: Times = Times(2)

@JvmInline
value class Times(val value: Int)

val Int.times: Times
    get() = Times(this)

val Int.time: Times
    get() = Times(this)

class Verification(private val receiver: Mockable, private val expectation: Expectation) {
    fun wasInvoked(atLeast: Times = once, atMost: Times? = null) {
        receiver.verify(RangeVerifier(expectation, atLeast.value, atMost?.value))
    }

    fun wasInvoked(exactly: Times) {
        receiver.verify(ExactVerifier(expectation, exactly.value))
    }

    fun wasMaybeInvoked(atMost: Times) {
        receiver.verify(RangeVerifier(expectation, null, atMost.value))
    }

    fun wasNotInvoked() {
        receiver.verify(ExactVerifier(expectation, 0))
    }
}
