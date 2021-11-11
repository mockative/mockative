package io.mockative

import kotlinx.atomicfu.atomic

internal class BlockingStub(val expectation: Expectation, private val invoke: (Array<Any?>) -> Any?) {
    var invocations: List<Invocation> by atomic(emptyList())

    fun invoke(invocation: Invocation): Any? {
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