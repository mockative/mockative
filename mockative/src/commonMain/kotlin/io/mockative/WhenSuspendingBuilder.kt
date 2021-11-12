package io.mockative

import io.mockative.matchers.AnyArgumentsMatcher
import io.mockative.matchers.ArgumentsMatcher
import io.mockative.matchers.Matcher
import io.mockative.matchers.SpecificArgumentsMatcher

fun whenSuspending(instance: Any, function: String): WhenSuspendingBuilder<Any?> {
    return WhenSuspendingBuilder(instance.asMockable(), function)
}

class WhenSuspendingBuilder<R>(
    private val mock: Mockable,
    private val function: String
) : AnySuspendResultBuilder<R> {
    fun with(vararg arguments: Matcher<Any?>): ResultBuilder {
        return ResultBuilder(SpecificArgumentsMatcher(arguments.map { it }))
    }

    fun then(block: suspend (arguments: Array<Any?>) -> R) {
        return ResultBuilder(AnyArgumentsMatcher).then(block)
    }

    override fun thenInvoke(block: suspend () -> R) = then { block() }

    inner class ResultBuilder(
        private val arguments: ArgumentsMatcher
    ) : AnySuspendResultBuilder<R> {

        fun then(block: suspend (arguments: Array<Any?>) -> R) {
            val expectation = Expectation.Function(function, arguments)
            val stub = SuspendStub(expectation, block)
            mock.addSuspendStub(stub)
        }

        override fun thenInvoke(block: suspend () -> R) = then { block() }
    }
}