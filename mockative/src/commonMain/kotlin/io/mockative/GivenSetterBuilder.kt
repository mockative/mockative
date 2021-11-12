package io.mockative

import io.mockative.matchers.Matcher

class GivenSetterBuilder<V>(private val mock: Mockable, private val property: String) {
    fun whenInvokedWith(value: Matcher<V>): ResultBuilder {
        return ResultBuilder(value)
    }

    @Suppress("UNCHECKED_CAST")
    inner class ResultBuilder(private val value: Matcher<V>) : AnyResultBuilder<Unit> {
        fun then(block: (V) -> Unit) {
            val expectation = Expectation.Setter(property, value)
            val stub = BlockingStub(expectation) { args ->
                block(args[0] as V)
            }
            mock.addBlockingStub(stub)
        }

        override fun thenInvoke(block: () -> Unit) = then { block() }
    }
}