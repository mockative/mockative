package io.mockative

/**
 * Verifies whether an invocation on the receiver was performed.
 */
fun <T : Any> verify(receiver: T): VerifyBuilder<T> {
    return VerifyBuilder(receiver)
}

/**
 * Verifies whether an invocation on the receiver was performed.
 */
fun <T : Any, R> verify(receiver: T, block: T.() -> R) {
    verify(receiver).at(least = 1, block = block)
}

/**
 * Confirms whether all invocations were verified using [verify].
 */
fun <T : Any> confirmVerified(receiver: T) {
    val mock = receiver.asMockable()
    mock.confirmVerified()
}