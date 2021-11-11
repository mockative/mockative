package io.mockative

import io.mockative.matchers.AnyArgumentsMatcher
import io.mockative.matchers.ArgumentsMatcher
import io.mockative.matchers.SpecificArgumentsMatcher

fun whenSuspending(instance: Any, function: String): WhenSuspendingBuilder<Any?> {
    return WhenSuspendingBuilder(instance.asMock(), function)
}

class WhenSuspendingBuilder<R>(private val mock: Mockable, private val function: String) {
    fun with(vararg arguments: Any?): ResultBuilder {
        return ResultBuilder(SpecificArgumentsMatcher(arguments.map { eq(it) }))
    }

    fun then(block: suspend (arguments: Array<Any?>) -> R) {
        return ResultBuilder(AnyArgumentsMatcher).then(block)
    }

    inner class ResultBuilder(private val arguments: ArgumentsMatcher) {
        fun then(block: suspend (arguments: Array<Any?>) -> R) {
            val expectation = Expectation.Function(function, arguments)
            val stub = SuspendStub(expectation, block)
            mock.addSuspendStub(stub)
        }
    }
}