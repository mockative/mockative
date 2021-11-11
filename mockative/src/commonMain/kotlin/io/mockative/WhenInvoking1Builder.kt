package io.mockative

import io.mockative.matchers.ArgumentsMatcher
import io.mockative.matchers.Matcher
import io.mockative.matchers.SpecificArgumentsMatcher
import kotlin.reflect.KFunction

fun <P1, R, F> whenInvoking(instance: Any, function: F): WhenInvoking1Builder<P1, R> where F : (P1) -> R, F : KFunction<R> {
    return whenInvoking1(instance, function)
}

fun <P1, R, F> whenInvoking1(instance: Any, function: F): WhenInvoking1Builder<P1, R> where F : (P1) -> R, F : KFunction<R> {
    return WhenInvoking1Builder(instance.asMock(), function)
}

class WhenInvoking1Builder<P1, R>(private val mock: Mockable, private val function: KFunction<R>) {
    fun with(p1: Matcher<P1>): ResultBuilder {
        val arguments = SpecificArgumentsMatcher(listOf(p1))
        return ResultBuilder(arguments)
    }

    inner class ResultBuilder(private val arguments: ArgumentsMatcher) : AnyResultBuilder<R> {
        fun then(block: (P1) -> R) {
            val expectation = Expectation.Function(function.name, arguments)
            val stub = BlockingStub(expectation) { args ->
                block(args[0] as P1)
            }
            mock.addBlockingStub(stub)
        }

        override fun then(block: () -> R) = then { _ -> block() }
    }
}