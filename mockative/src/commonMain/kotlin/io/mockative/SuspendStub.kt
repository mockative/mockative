package io.mockative

import io.mockative.concurrency.AtomicList

internal typealias SuspendAnswer = suspend (Array<Any?>) -> Any?

internal abstract class SuspendStub(val expectation: Expectation) {
    val invocations = AtomicList<Invocation>()

    abstract fun matches(invocation: Invocation): Boolean

    abstract suspend fun invoke(arguments: Array<Any?>): Any?

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

internal class OpenSuspendStub(expectation: Expectation, private val answer: SuspendAnswer) :
    SuspendStub(expectation) {
    override fun matches(invocation: Invocation): Boolean {
        return expectation.matches(invocation)
    }

    override suspend fun invoke(arguments: Array<Any?>): Any? {
        return answer(arguments)
    }
}

internal class ClosedSuspendStub(expectation: Expectation, private val answers: List<SuspendAnswer>) :
    SuspendStub(expectation) {
    override fun matches(invocation: Invocation): Boolean {
        return expectation.matches(invocation) && invocations.size < answers.size
    }

    override suspend fun invoke(arguments: Array<Any?>): Any? {
        return answers[invocations.size](arguments)
    }
}
