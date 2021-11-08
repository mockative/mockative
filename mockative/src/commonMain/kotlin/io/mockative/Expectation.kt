package io.mockative

interface Expectation<T : Any> {
    val instance: T
    var invocation: Invocation
    var result: ExpectationResult<T>?
    var invocations: Int

    fun close()
}