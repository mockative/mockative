package io.mockative

import io.mockative.matchers.ArgumentsMatcher
import io.mockative.matchers.Matcher
import io.mockative.matchers.SpecificArgumentsMatcher
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty

fun <T : Any> given(receiver: T): GivenBuilder<T> = GivenBuilder(receiver)

inline fun <T : Any> givenMock(receiver: T): GivenBuilder<T> = given(receiver)
inline fun <T : Any> setup(receiver: T): GivenBuilder<T> = given(receiver)

class GivenBuilder<T : Any>(private val receiver: T) {
    suspend fun <R> coroutine(block: suspend T.() -> R): SuspendResultBuilder<R> {
        val mock = receiver.asMockable()
        val invocation = mock.record(block)
        val expectation = invocation.toExpectation()
        return SuspendResultBuilder(mock, expectation)
    }

    fun <R> invocation(block: T.() -> R): ResultBuilder<R> {
        val mock = receiver.asMockable()
        val invocation = mock.record(block)
        val expectation = invocation.toExpectation()
        return ResultBuilder(mock, expectation)
    }

    fun <R, F> function(function: F): GivenFunction0Builder<R> where F : () -> R, F : KFunction<R> = TODO()
    fun <R, F> function(function: F, type: KFunction0): GivenFunction0Builder<R> where F : () -> R, F : KFunction<R> = TODO()
    fun <P1, R, F> function(function: F): GivenFunction1Builder<P1, R> where F : (P1) -> R, F : KFunction<R> = TODO()
    fun <P1, R, F> function(function: F, type: KFunction1<P1>): GivenFunction1Builder<P1, R> where F : (P1) -> R, F : KFunction<R> = TODO()

    fun <R, F> suspendFunction(function: F): GivenSuspendFunction0Builder<R> where F : suspend () -> R, F : KFunction<R> = TODO()
    fun <R, F> suspendFunction(function: F, type: KFunction0): GivenSuspendFunction0Builder<R> where F : suspend () -> R, F : KFunction<R> = TODO()
    fun <P1, P2, R, F> suspendFunction(function: F): GivenSuspendFunction2Builder<P1, P2, R> where F : suspend (P1, P2) -> R, F : KFunction<R> = TODO()
    fun <P1, P2, R, F> suspendFunction(function: F, type: KFunction2<P1, P2>): GivenSuspendFunction2Builder<P1, P2, R> where F : suspend (P1, P2) -> R, F : KFunction<R> = TODO()

    fun <V> getter(property: KProperty<V>): GivenFunction0Builder<V> = TODO()
    fun <V> setter(property: KMutableProperty<V>): GivenFunction1Builder<V, Unit> = TODO()
}

object KFunction0
inline fun fun0() = KFunction0

@Suppress("unused")
class KFunction1<P1>
inline fun <reified P1> fun1() = KFunction1<P1>()

@Suppress("unused")
class KFunction2<P1, P2>
inline fun <reified P1, P2> fun2() = KFunction2<P1, P2>()

class GivenFunction0Builder<R> {
    fun whenInvoked(): ResultBuilder {
        TODO()
    }

    inner class ResultBuilder : AnyResultBuilder<R> {
        fun then(block: () -> R) {
            TODO()
        }

        override fun thenInvoke(block: () -> R) = then { block() }
    }
}

class GivenFunction1Builder<P1, R> {
    fun whenInvokedWith(p1: Matcher<P1>): ResultBuilder {
        TODO()
    }

    inner class ResultBuilder : AnyResultBuilder<R> {
        fun then(block: (P1) -> R) {
            TODO()
        }

        override fun thenInvoke(block: () -> R) = then { block() }
    }
}

class GivenSuspendFunction0Builder<R> {
    fun whenInvoked(): ResultBuilder {
        val arguments = SpecificArgumentsMatcher(emptyList())
        return ResultBuilder(arguments)
    }

    @Suppress("UNCHECKED_CAST")
    inner class ResultBuilder(private val arguments: ArgumentsMatcher) : AnySuspendResultBuilder<R> {
        fun then(block: suspend () -> R) {
            TODO()
        }

        override fun thenInvoke(block: suspend () -> R) = then { block() }
    }
}

class GivenSuspendFunction2Builder<P1, P2, R> {
    fun whenInvokedWith(p1: Matcher<P1>, p2: Matcher<P2>): ResultBuilder {
        val arguments = SpecificArgumentsMatcher(listOf(p1, p2))
        return ResultBuilder(arguments)
    }

    @Suppress("UNCHECKED_CAST")
    inner class ResultBuilder(private val arguments: ArgumentsMatcher) : AnySuspendResultBuilder<R> {
        fun then(block: suspend (P1, P2) -> R) {
            TODO()
        }

        override fun thenInvoke(block: suspend () -> R) = then { _, _ -> block() }
    }
}

suspend fun demo2(service: DemoService) {
    given(service).invocation { service.transformData("abc") }
        .thenReturn("abc")

    given(service).coroutine { service.refreshData() }
        .thenDoNothing()

    given(service).invocation { service.mutableProperty }
        .thenReturn("abc")

    given(service).invocation { service.mutableProperty = "abc" }
        .thenDoNothing()

    givenMock(service).function(service::transformData)
        .whenInvokedWith(any())
        .thenReturn("abc")

    setup(service).suspendFunction(service::refreshData)
        .whenInvoked()
        .thenDoNothing()

    given(service).suspendFunction(service::fetchData)
        .whenInvokedWith(any(), any())
        .thenDoNothing()

    var mutableProperty = "defaultValue"

    given(service).getter(service::mutableProperty)
        .whenInvoked()
        .then { mutableProperty }

    given(service).setter(service::mutableProperty)
        .whenInvokedWith(any())
        .then { mutableProperty = it }

    given(service).suspendFunction(service::overload, fun2<Int, String>())
}
