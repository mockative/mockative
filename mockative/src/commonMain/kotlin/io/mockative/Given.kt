package io.mockative

fun <T : Any> given(receiver: T): GivenBuilder<T> = GivenBuilder(receiver)
