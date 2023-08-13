package io.mockative

import io.mockative.concurrency.AtomicList

typealias BlockingAnswer = (Array<Any?>) -> Any?

internal abstract class BlockingStub(val expectation: Expectation) {
    val invocations = AtomicList<Invocation>()

    abstract fun matches(invocation: Invocation): Boolean

    abstract fun invoke(arguments: Array<Any?>): Any?

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

internal class OpenBlockingStub(expectation: Expectation, private val answer: BlockingAnswer) :
    BlockingStub(expectation) {
    override fun matches(invocation: Invocation): Boolean {
        return expectation.matches(invocation)
    }

    override fun invoke(arguments: Array<Any?>): Any? {
        return answer(arguments)
    }
}

internal class ClosedBlockingStub(expectation: Expectation, private val answers: List<BlockingAnswer>) :
    BlockingStub(expectation) {
    override fun matches(invocation: Invocation): Boolean {
        return expectation.matches(invocation) && invocations.size < answers.size
    }

    override fun invoke(arguments: Array<Any?>): Any? {
        return answers[invocations.size](arguments)
    }
}
