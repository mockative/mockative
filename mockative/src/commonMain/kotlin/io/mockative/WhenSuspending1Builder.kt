package io.mockative

import io.mockative.matchers.ArgumentsMatcher
import io.mockative.matchers.Matcher
import io.mockative.matchers.SpecificArgumentsMatcher
import kotlin.reflect.KFunction

fun <P1, R, F> whenSuspending(instance: Any, function: F): WhenSuspending1Builder<P1, R> where F : suspend (P1) -> R, F : KFunction<R> {
    return whenSuspending1(instance, function)
}

fun <P1, R, F> whenSuspending1(instance: Any, function: F): WhenSuspending1Builder<P1, R> where F : suspend (P1) -> R, F : KFunction<R> {
    return WhenSuspending1Builder(instance.asMockable(), function)
}

class WhenSuspending1Builder<P1, R>(private val mock: Mockable, private val function: KFunction<R>) {
    fun with(p1: Matcher<P1>): ResultBuilder {
        val arguments = SpecificArgumentsMatcher(listOf(p1))
        return ResultBuilder(arguments)
    }

    @Suppress("UNCHECKED_CAST")
    inner class ResultBuilder(private val arguments: ArgumentsMatcher) : AnySuspendResultBuilder<R> {
        fun then(block: suspend (P1) -> R) {
            val expectation = Expectation.Function(function.name, arguments)
            val stub = SuspendStub(expectation) { args ->
                block(args[0] as P1)
            }
            mock.addSuspendStub(stub)
        }

        override fun thenInvoke(block: suspend () -> R) = then { block() }
    }
}