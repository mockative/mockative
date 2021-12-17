package io.mockative

import io.mockative.concurrency.AtomicList

internal class BlockingStub(val expectation: Expectation, private val invoke: (Array<Any?>) -> Any?) {
    val invocations = AtomicList<Invocation>()

    fun invoke(invocation: Invocation): Any? {
        val arguments = when (invocation) {
            is Invocation.Function -> invocation.arguments.toTypedArray()
            is Invocation.Getter -> emptyArray()
            is Invocation.Setter -> arrayOf(invocation.value)
        }

        try {
            return invoke(arguments)
        } finally {
            invocations.add(invocation)
        }
    }
}