package io.mockative

import io.mockative.matchers.ArgumentsMatcher
import io.mockative.matchers.Matcher
import io.mockative.matchers.SpecificArgumentsMatcher
import kotlin.reflect.KFunction

fun <P1, P2, R, F> whenSuspending(
    instance: Any,
    function: F
): WhenSuspending2Builder<P1, P2, R> where F : suspend (P1, P2) -> R, F : KFunction<R> {
    return whenSuspending2(instance, function)
}

fun <P1, P2, R, F> whenSuspending2(
    instance: Any,
    function: F
): WhenSuspending2Builder<P1, P2, R> where F : suspend (P1, P2) -> R, F : KFunction<R> {
    return WhenSuspending2Builder(instance.asMockable(), function)
}

class WhenSuspending2Builder<P1, P2, R>(
    private val mock: Mockable,
    private val function: KFunction<R>
) {
    fun with(p1: Matcher<P1>, p2: Matcher<P2>): ResultBuilder {
        val arguments = SpecificArgumentsMatcher(listOf(p1, p2))
        return ResultBuilder(arguments)
    }

    inner class ResultBuilder(private val arguments: ArgumentsMatcher) :
        AnySuspendResultBuilder<R> {
        fun then(block: suspend (P1, P2) -> R) {
            val expectation = Expectation.Function(function.name, arguments)
            val stub = SuspendStub(expectation) { args ->
                block(args[0] as P1, args[1] as P2)
            }
            mock.addSuspendStub(stub)
        }

        override fun thenInvoke(block: suspend () -> R) = then { _, _ -> block() }
    }
}