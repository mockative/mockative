package dk.nillerr.mockative

import kotlinx.coroutines.launch

fun <T : Any, R> given(receiver: T, block: suspend T.() -> R): ExpectationBuilder<R> {
    val mock = receiver as? Mocked ?: throw GivenNonMockError(receiver)

    val builder = ExpectationBuilder<R>(mock)

    mock.expectation = builder

    mock.scope.launch {
        try {
            block(receiver)
        } catch (ex: MockingInProgressError) {
            mock.expectation = null
        } catch (ex: Throwable) {
            throw ex
        }
    }

    mock.expectations.add(builder)

    return builder
}

fun <T : Any> verify(receiver: T) {
    val mock = receiver as? Mocked ?: throw VerifyNonMockError(receiver)
    mock.expectations.forEach { it.verify() }
}
