package io.mockative

import io.mockative.concurrency.atomic

internal class SuspendStub(val expectation: Expectation, private val invoke: suspend (Array<Any?>) -> Any?) {
    var invocations: List<Invocation> by atomic(emptyList())

    suspend fun invoke(invocation: Invocation): Any? {
        val arguments = when (invocation) {
            is Invocation.Function -> invocation.arguments.toTypedArray()
            is Invocation.Getter -> emptyArray()
            is Invocation.Setter -> arrayOf(invocation.value)
        }

        val result = invoke(arguments)
        invocations = invocations + invocation
        return result
    }
}
