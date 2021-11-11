package io.mockative

import io.mockative.matchers.AnyArgumentsMatcher
import io.mockative.matchers.ArgumentsMatcher
import io.mockative.matchers.SpecificArgumentsMatcher

fun whenInvoking(instance: Any, function: String): WhenInvokingBuilder<Any?> {
    return WhenInvokingBuilder(instance.asMockable(), function)
}

class WhenInvokingBuilder<R>(private val mock: Mockable, private val function: String) : AnyResultBuilder<R> {
    fun with(vararg arguments: Any?): ResultBuilder {
        val matcher = SpecificArgumentsMatcher(arguments.map { eq(it) })
        return ResultBuilder(matcher)
    }

    fun then(block: (arguments: Array<Any?>) -> R) {
        return ResultBuilder(AnyArgumentsMatcher).then(block)
    }

    override fun thenInvoke(block: () -> R) = then { block() }

    inner class ResultBuilder(private val arguments: ArgumentsMatcher) : AnyResultBuilder<R> {

        fun then(block: (arguments: Array<Any?>) -> R) {
            val expectation = Expectation.Function(function, arguments)
            val stub = BlockingStub(expectation, block)
            mock.addBlockingStub(stub)
        }
        override fun thenInvoke(block: () -> R) = then { block() }
    }
}