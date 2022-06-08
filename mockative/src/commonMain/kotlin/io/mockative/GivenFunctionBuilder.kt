package io.mockative

import io.mockative.matchers.ArgumentsMatcher
import io.mockative.matchers.Matcher
import io.mockative.matchers.SpecificArgumentsMatcher

class GivenFunctionBuilder<R>(private val mock: Mockable, private val name: String) {
    fun whenInvokedWith(vararg matchers: Matcher<*>): ResultBuilder {
        val arguments = SpecificArgumentsMatcher(matchers.toList())
        return ResultBuilder(arguments)
    }

    inner class ResultBuilder(private val arguments: ArgumentsMatcher) : AnyResultBuilder<R> {
        fun then(block: (args: Array<Any?>) -> R) {
            val expectation = Expectation.Function(name, arguments)
            val stub = BlockingStub(expectation, block)
            mock.addBlockingStub(stub)
        }

        override fun thenInvoke(block: () -> R) = then { block() }
    }
}
