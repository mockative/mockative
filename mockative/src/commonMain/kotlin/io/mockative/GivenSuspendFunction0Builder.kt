package io.mockative

import io.mockative.matchers.ArgumentsMatcher
import io.mockative.matchers.SpecificArgumentsMatcher
import kotlin.reflect.KFunction

class GivenSuspendFunction0Builder<R>(private val mock: Mockable, private val function: KFunction<R>) {
    fun whenInvoked(): ResultBuilder {
        val arguments = SpecificArgumentsMatcher(emptyList())
        return ResultBuilder(arguments)
    }

    @Suppress("UNCHECKED_CAST")
    inner class ResultBuilder(private val arguments: ArgumentsMatcher) : AnySuspendResultBuilder<R> {
        fun then(block: suspend () -> R) {
            val expectation = Expectation.Function(function.name, arguments)
            val stub = SuspendStub(expectation) { args ->
                block()
            }
            mock.addSuspendStub(stub)
        }

        override fun thenInvoke(block: suspend () -> R) = then { block() }
    }
}