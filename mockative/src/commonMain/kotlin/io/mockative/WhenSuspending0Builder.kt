package io.mockative

import io.mockative.matchers.SpecificArgumentsMatcher
import kotlin.reflect.KFunction

fun <R, F> whenSuspending(
    instance: Any,
    function: F
): WhenSuspending0Builder<R> where F : suspend () -> R, F : KFunction<R> {
    return whenSuspending0(instance, function)
}

fun <R, F> whenSuspending0(
    instance: Any,
    function: F
): WhenSuspending0Builder<R> where F : suspend () -> R, F : KFunction<R> {
    return WhenSuspending0Builder(instance.asMockable(), function)
}

class WhenSuspending0Builder<R>(
    private val mock: Mockable,
    private val function: KFunction<R>
) : AnySuspendResultBuilder<R> {
    fun then(block: suspend () -> R) {
        val arguments = SpecificArgumentsMatcher(emptyList())
        val expectation = Expectation.Function(function.name, arguments)
        val stub = SuspendStub(expectation) { block() }
        mock.addSuspendStub(stub)
    }

    override fun thenInvoke(block: suspend () -> R) = then(block)
}
