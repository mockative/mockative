//package io.mockative.lab
//
//import io.mockative.*
//import kotlin.reflect.KCallable
//import kotlin.reflect.KMutableProperty
//import kotlin.reflect.KProperty
//
//inline fun <T : Any, S : Stubber> given(mock: T, `when`: T.() -> S): S = TODO()
//
//inline fun <T : Any, V> T.whenSetting(property: KMutableProperty<V>): SetterStubber<T, V> = TODO()
//
//inline fun <T : Any, V> T.whenGetting(property: KProperty<V>): GetterStubber<T, V> = TODO()
//
//inline fun <T : Any, R, F> T.whenInvoking(function: F)
//        where F : () -> R, F : KCallable<R> = whenInvoking0(function)
//
//inline fun <T : Any, R, F> T.whenInvoking0(function: F): FunctionStubber0<T, R>
//        where F : () -> R, F : KCallable<R> = TODO()
//
//inline fun <T : Any, P1, P2, P3, R, F> T.whenInvoking(function: F)
//        where F : (P1, P2, P3) -> R, F : KCallable<R> = whenInvoking3(function)
//
//inline fun <T : Any, P1, P2, P3, R, F> T.whenInvoking3(function: F): FunctionStubber3<T, P1, P2, P3, R>
//        where F : (P1, P2, P3) -> R, F : KCallable<R> = TODO()
//
//inline fun <T : Any, P1, P2, P3, R, F> T.whenInvoking(function: F)
//        where F : suspend (P1, P2, P3) -> R, F : KCallable<R> = whenInvoking3(function)
//
//inline fun <T : Any, P1, P2, P3, R, F> T.whenInvoking3(function: F): SuspendFunctionStubber3<T, P1, P2, P3, R>
//        where F : suspend (P1, P2, P3) -> R, F : KCallable<R> = TODO()
//
//inline fun <T : Any, P1, P2, P3, P4, R, F> T.whenInvoking(function: F)
//        where F : (P1, P2, P3, P4) -> R, F : KCallable<R> = whenInvoking4(function)
//
//inline fun <T : Any, P1, P2, P3, P4, R, F> T.whenInvoking4(function: F): FunctionStubber4<T, P1, P2, P3, P4, R>
//        where F : (P1, P2, P3, P4) -> R, F : KCallable<R> = TODO()
//
//interface Stubber
//
//class FunctionStubber0<T : Any, R> : Stubber {
//    fun then(block: () -> R): Unit = TODO()
//}
//
//abstract class AbstractFunctionStubber3<S : AbstractFunctionStubber3<S, T, P1, P2, P3, R>, T : Any, P1, P2, P3, R> : Stubber {
//    abstract val stubber: S
//
//    fun with(p1: Matcher<P1>, p2: Matcher<P2>, p3: Matcher<P3>): S {
//        return stubber
//    }
//}
//
//class FunctionStubber3<T : Any, P1, P2, P3, R> : AbstractFunctionStubber3<FunctionStubber3<T, P1, P2, P3, R>, T, P1, P2, P3, R>() {
//    override val stubber = this
//
//    fun then(block: (P1, P2, P3) -> R): Unit = TODO()
//    fun thenReturn(returnValue: R): Unit = TODO()
//}
//
//class SuspendFunctionStubber3<T : Any, P1, P2, P3, R> : Stubber {
//    fun then(block: suspend (P1, P2, P3) -> R): Unit = TODO()
//    fun thenReturn(returnValue: R): Unit = TODO()
//}
//
//class FunctionStubber4<T : Any, P1, P2, P3, P4, R> : Stubber {
//    fun then(block: (P1, P2, P3, P4) -> R): Unit = TODO()
//    fun thenReturn(returnValue: R): Unit = TODO()
//}
//
//class SetterStubber<T : Any, V> : Stubber {
//    fun to(value: Matcher<V>): SetterStubber<T, V> = TODO()
//
//    fun then(block: () -> Unit): Unit = TODO()
//}
//
//class GetterStubber<T : Any, V> : Stubber {
//    fun then(block: () -> V): Unit = TODO()
//    fun thenReturn(value: V): Unit = TODO()
//}
//
//fun FooTube(mockService: MockService) {
//    given(mockService) { whenSetting(::writeable) }
//        .to(any())
//        .then { print("set") }
//
//    given(mockService) { whenGetting(::writeable) }
//        .thenReturn("abc")
//
//    given(mockService) { whenGetting(::readable) }
//        .thenReturn("abc")
//
//    given(mockService) { whenInvoking3(this::doStuff) }
//        .with(anyOf(1, 2, 5), eq("abc"), any())
//
//    given(mockService) { whenInvoking3(this::doStuffLater) }
//}
//
