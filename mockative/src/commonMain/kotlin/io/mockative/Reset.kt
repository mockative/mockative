package io.mockative

/**
 * Resets a mock by removing all stubs, recorded invocations and verified invocations.
 *
 * @param receiver The mock to reset
 */
fun <T : Any> reset(receiver: T) {
    val mock = receiver.asMockable()
    mock.reset()
}
