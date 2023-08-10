package io.mockative

import io.mockative.concurrency.AtomicList

internal class SuspendStub(val expectation: Expectation, private val invoke: suspend (Array<Any?>) -> Any?) {
    val invocations = AtomicList<Invocation>()

    suspend fun invoke(invocation: Invocation): Any? {
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

    override fun toString(): String {
        return buildString {
            appendLine("$expectation")
            appendLine("  Invocations")

            invocations.forEach {
                appendLine(it.toString().prependIndent("    "))
            }
        }
    }
}
