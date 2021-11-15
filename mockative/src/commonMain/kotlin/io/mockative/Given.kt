package io.mockative

fun <T : Any> given(receiver: T): GivenBuilder<T> = GivenBuilder(receiver)
fun <T : Any> expect(receiver: T): GivenBuilder<T> = GivenBuilder(receiver)
fun <T : Any> givenMock(receiver: T): GivenBuilder<T> = GivenBuilder(receiver)
fun <T : Any> setup(receiver: T): GivenBuilder<T> = GivenBuilder(receiver)
