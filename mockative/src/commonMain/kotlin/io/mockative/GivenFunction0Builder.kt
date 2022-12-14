package io.mockative

import io.mockative.matchers.ArgumentsMatcher
import io.mockative.matchers.SpecificArgumentsMatcher
import kotlin.reflect.KFunction

class GivenFunction0Builder<R>(private val mock: Mockable, private val function: KFunction<R>) {
    fun whenInvoked(): ResultBuilder {
        val arguments = SpecificArgumentsMatcher(emptyList())
        return ResultBuilder(arguments)
    }

    inner class ResultBuilder(private val arguments: ArgumentsMatcher) : AnyResultBuilder<R> {
        fun then(block: () -> R) {
            val expectation = Expectation.Function(function.name, arguments)
            val stub = BlockingStub(expectation) { _ ->
                block()
            }
            mock.addBlockingStub(stub)
        }

        override fun thenInvoke(block: () -> R) = then { block() }
    }
}

