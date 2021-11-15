package io.mockative

fun <T : Any> reset(receiver: T) {
    val mock = receiver.asMockable()
    mock.reset()
}