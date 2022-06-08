package io.mockative

import io.mockative.matchers.ArgumentsMatcher
import io.mockative.matchers.Matcher
import io.mockative.matchers.SpecificArgumentsMatcher

class GivenSuspendFunctionBuilder<R>(private val mock: Mockable, private val name: String) {
    fun whenInvokedWith(vararg matchers: Matcher<*>): ResultBuilder {
        val arguments = SpecificArgumentsMatcher(matchers.toList())
        return ResultBuilder(arguments)
    }

    @Suppress("UNCHECKED_CAST")
    inner class ResultBuilder(private val arguments: ArgumentsMatcher) : AnySuspendResultBuilder<R> {
        fun then(block: suspend (args: Array<Any?>) -> R) {
            val expectation = Expectation.Function(name, arguments)
            val stub = SuspendStub(expectation, block)
            mock.addSuspendStub(stub)
        }

        override fun thenInvoke(block: suspend () -> R) = then { block() }
    }
}
