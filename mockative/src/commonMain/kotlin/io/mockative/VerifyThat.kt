package io.mockative

fun <T : Any> verifyThat(mock: T): VerifyThatBuilder<T> {
    return VerifyThatBuilder(mock)
}
