package io.mockative

import io.mockative.matchers.ArgumentsMatcher
import io.mockative.matchers.Matcher
import io.mockative.matchers.SpecificArgumentsMatcher
import kotlin.reflect.KFunction

fun <P1, P2, R, F> whenInvoking(instance: Any, function: F): WhenInvoking2Builder<P1, P2, R> where F : (P1, P2) -> R, F : KFunction<R> {
    return whenInvoking2(instance, function)
}

fun <P1, P2, R, F> whenInvoking2(instance: Any, function: F): WhenInvoking2Builder<P1, P2, R> where F : (P1, P2) -> R, F : KFunction<R> {
    return WhenInvoking2Builder(instance.asMockable(), function)
}

class WhenInvoking2Builder<P1, P2, R>(private val mock: Mockable, private val function: KFunction<R>) {
    fun with(p1: Matcher<P1>, p2: Matcher<P2>): ResultBuilder {
        val arguments = SpecificArgumentsMatcher(listOf(p1, p2))
        return ResultBuilder(arguments)
    }

    inner class ResultBuilder(private val arguments: ArgumentsMatcher) : AnyResultBuilder<R> {
        fun then(block: (P1, P2) -> R) {
            val expectation = Expectation.Function(function.name, arguments)
            val stub = BlockingStub(expectation) { args ->
                block(args[0] as P1, args[1] as P2)
            }
            mock.addBlockingStub(stub)
        }

        override fun thenInvoke(block: () -> R) = then { _, _ -> block() }
    }
}