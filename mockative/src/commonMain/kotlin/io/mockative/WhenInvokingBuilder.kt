package io.mockative

import io.mockative.matchers.AnyArgumentsMatcher
import io.mockative.matchers.ArgumentsMatcher
import io.mockative.matchers.SpecificArgumentsMatcher

fun whenInvoking(instance: Any, function: String): WhenInvokingBuilder<Any?> {
    return WhenInvokingBuilder(instance.asMock(), function)
}

class WhenInvokingBuilder<R>(private val mock: Mockable, private val function: String) {
    fun with(vararg arguments: Any?): ResultBuilder {
        val matcher = SpecificArgumentsMatcher(arguments.map { eq(it) })
        return ResultBuilder(matcher)
    }

    fun then(block: (arguments: Array<Any?>) -> R) {
        return ResultBuilder(AnyArgumentsMatcher).then(block)
    }

    inner class ResultBuilder(private val arguments: ArgumentsMatcher) {
        fun then(block: (arguments: Array<Any?>) -> R) {
            val expectation = Expectation.Function(function, arguments)
            val stub = BlockingStub(expectation, block)
            mock.addBlockingStub(stub)
        }
    }
}