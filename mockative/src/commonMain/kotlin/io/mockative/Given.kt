package io.mockative

fun <T : Any> given(receiver: T): GivenBuilder<T> = GivenBuilder(receiver)

inline fun <T : Any> givenMock(receiver: T): GivenBuilder<T> = given(receiver)
inline fun <T : Any> setup(receiver: T): GivenBuilder<T> = given(receiver)
