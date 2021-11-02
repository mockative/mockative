package dk.nillerr.mockative

import kotlinx.coroutines.launch

/**
 * Stubs the invocation of a member on a mock.
 *
 * @param receiver the mock to stub a member of.
 * @param block the block containing the invocation of the member to mock.
 * @param T the type being mocked.
 * @param R the return type of the member being mocked.
 *
 * @throws VerifyNonMockError the [receiver] was not a generated mock instance.
 */
@Suppress("UNCHECKED_CAST")
fun <T : Any, R> given(receiver: T, block: suspend T.() -> R): ExpectationBuilder<T, R> {
    val mock = receiver as? Mocked<T> ?: throw GivenNonMockError(receiver)

    val builder = ExpectationBuilder<T, R>(receiver)

    mock.expectation = builder

    mock.scope.launch {
        try {
            block(receiver)
        } catch (ex: MockingInProgressError) {
            builder.invocation = ex.invocation

            mock.expectation = null
        } catch (ex: Throwable) {
            throw ex
        }
    }

    mock.expectations.add(builder)

    return builder
}

/**
 * Verifies all expectations on a mock was met at least once.
 *
 * @param receiver the mock to stub a member of.
 * @param T the type being mocked.
 *
 * @throws VerifyNonMockError the [receiver] was not a generated mock instance.
 * @throws ExpectationNotMetError an expectation on the mock was not met.
 */
fun <T : Any> verify(receiver: T) {
    val mock = receiver as? Mocked<*> ?: throw VerifyNonMockError(receiver)
    mock.expectations.forEach { it.verify() }
}
