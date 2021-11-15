package io.mockative

fun <T : Any> verifyThat(mock: T): VerifyThatBuilder<T> = VerifyThatBuilder(mock)
fun <T : Any> verify(mock: T): VerifyThatBuilder<T> = VerifyThatBuilder(mock)
