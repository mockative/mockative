package io.mockative

import io.mockative.matchers.SpecificArgumentsMatcher
import kotlin.reflect.KFunction

fun <R, F> whenInvoking(instance: Any, function: F): WhenInvoking0Builder<R> where F : () -> R, F : KFunction<R> {
    return whenInvoking0(instance, function)
}

fun <R, F> whenInvoking0(instance: Any, function: F): WhenInvoking0Builder<R> where F : () -> R, F : KFunction<R> {
    return WhenInvoking0Builder(instance.asMock(), function)
}

class WhenInvoking0Builder<R>(private val mock: Mockable, private val function: KFunction<R>) {
    fun then(block: () -> R) {
        val arguments = SpecificArgumentsMatcher(emptyList())
        val expectation = Expectation.Function(function.name, arguments)
        val stub = BlockingStub(expectation) { block() }
        mock.addBlockingStub(stub)
    }
}