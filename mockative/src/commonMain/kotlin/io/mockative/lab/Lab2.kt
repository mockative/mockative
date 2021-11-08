//package io.mockative.lab
//
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlin.coroutines.CoroutineContext
//import kotlin.native.concurrent.ThreadLocal
//import kotlin.reflect.KFunction
//import kotlin.reflect.KMutableProperty
//import kotlin.reflect.KProperty
//
//
//@ThreadLocal
//object StubStore {
//    fun <T : Any, R> getFunction(
//        instance: T,
//        function: KFunction<R>,
//        arguments: Array<Any?>
//    ): Stub.Function<T, R> = TODO()
//
//    init {
//        val scope = CoroutineScope(Dispatchers.Main)
//        scope.launch {
//
//        }
//    }
//}
//
//sealed class Stub {
//    data class Function<T : Any, R>(
//        val instance: T,
//        val function: KFunction<R>,
//        val predicate: (Array<Any?>) -> Boolean,
//        val implementation: () -> R
//    ) : Stub()
//
//    data class Setter<T : Any, V>(
//        val instance: T,
//        val property: KMutableProperty<V>,
//        val predicate: (V?) -> Boolean,
//        val implementation: () -> Unit
//    ) : Stub()
//
//    data class Getter<T : Any, V>(
//        val instance: T,
//        val property: KProperty<V>,
//        val implementation: () -> V
//    ) : Stub()
//}