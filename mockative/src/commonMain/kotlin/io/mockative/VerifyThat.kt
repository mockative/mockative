package io.mockative

import io.mockative.matchers.Matcher
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty

class VerifyThatBuilder<T : Any>(private val receiver: T) {
    fun <R> invocation(block: T.() -> R): Verification<T, R> = TODO()

    suspend fun <R> coroutine(block: suspend T.() -> R): Verification<T, R> = TODO()

    fun function(function: String): VerifyThatWithBuilder<T, Any?> = TODO()
    fun <R, F> function(function: F): Verification<T, R> where F : () -> R, F : KFunction<R> = TODO()
    fun <P1, R, F> function(function: F): VerifyThatWith1Builder<P1, T, R> where F : (P1) -> R, F : KFunction<R> = TODO()
    fun <P1, P2, R, F> function(function: F): VerifyThatWith2Builder<P1, P2, T, R> where F : (P1, P2) -> R, F : KFunction<R> = TODO()

    fun suspendFunction(function: String): Verification<T, Any?> = TODO()
    fun <R, F> suspendFunction(function: F): Verification<T, R> where F : suspend () -> R, F : KFunction<R> = TODO()
    fun <P1, R, F> suspendFunction(function: F): VerifyThatWith1Builder<P1, T, R> where F : suspend (P1) -> R, F : KFunction<R> = TODO()
    fun <P1, P2, R, F> suspendFunction(function: F): VerifyThatWith2Builder<P1, P2, T, R> where F : suspend (P1, P2) -> R, F : KFunction<R> = TODO()

    fun <V> getter(getter: KProperty<V>): Verification<T, V> = TODO()

    fun <V> setter(setter: KMutableProperty<V>): VerifyThatWith1Builder<V, T, Unit> = TODO()
}

class VerifyThatWithBuilder<T : Any, R>(private val receiver: T) {
    fun with(vararg arguments: Matcher<*>): Verification<T, R> {
        TODO()
    }
}

class VerifyThatWith1Builder<P1, T : Any, R>(private val receiver: T) {
    fun with(p1: Matcher<P1>): Verification<T, R> {
        TODO()
    }
}

class VerifyThatWith2Builder<P1, P2, T : Any, R>(private val receiver: T) {
    fun with(p1: Matcher<P1>, p2: Matcher<P2>): Verification<T, R> {
        TODO()
    }
}

class Verification<T : Any, R>(private val receiver: T) {
    fun wasInvoked(atLeast: Int? = null, atMost: Int? = null) {
        TODO()
    }

    fun wasInvoked(exactly: Int) {
        TODO()
    }

    fun wasNotInvoked() {
        TODO()
    }
}

fun <T : Any> verifyThat(mock: T): VerifyThatBuilder<T> {
    TODO()
}

interface DemoService {
    var mutableProperty: String

    fun transformData(data: String): String

    suspend fun refreshData()
    suspend fun fetchData(id: String, name: String)
}

suspend fun demo(service: DemoService) {
//    given(service).invocation { service.transformData("abc") }
//        .thenReturn("abc")
//
//    given(service).coroutine { service.refreshData() }
//        .thenDoNothing()
//
//    given(service).function(service::transformData)
//        .whenInvokedWith(any())
//        .thenReturn("abc")
//
//    given(service).getter(service::mutableProperty)
//        .whenInvoked()
//        .thenReturn("abc")
//
//    given(service).setter(service::mutableProperty)
//        .whenInvokedWith(any())
//        .thenReturn("abc")
//
//    verifyThat(service).invocation { transformData("abc") }
//        .wasInvoked()
//
//    verifyThat(service).coroutine { refreshData() }
//        .wasNotInvoked()
//
//    verifyThat(service).function("transformData")
//        .with(any<String>(), any<Int>())
//        .wasNotInvoked()
//
//    verifyThat(service).function(service::transformData)
//        .with(any())
//        .wasNotInvoked()
//
//    verifyThat(service).suspendFunction(service::refreshData)
//        .wasInvoked(exactly = 8)
//
//    verifyThat(service).suspendFunction(service::fetchData)
//        .with(any(), oneOf("a", "b", "c"))
//        .wasInvoked(atMost = 9)
//
//    verifyThat(service).getter(service::mutableProperty)
//        .wasInvoked(atLeast = 5)
//
//    verifyThat(service).setter(service::mutableProperty)
//        .with(eq("abc"))
//        .wasInvoked(exactly = 1)
}