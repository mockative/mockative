package io.mockative

/**
 * Validates a mock, checking whether it contains any expectations without matching invocations
 * (unmet expectations).
 */
fun <T : Any> validate(receiver: T) {
    val mock = receiver.asMockable()
    mock.validate()
}