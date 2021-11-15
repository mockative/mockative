package io.mockative

fun <T : Any> verify(mock: T): VerifyThatBuilder<T> = VerifyThatBuilder(mock)
