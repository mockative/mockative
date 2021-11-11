package io.mockative

import kotlin.reflect.KProperty

fun <V, P> whenGetting(instance: Any, getter: P): WhenGettingBuilder<V> where P : KProperty<V> {
    return WhenGettingBuilder(instance.asMock(), getter)
}

class WhenGettingBuilder<V>(private val mock: Mockable, private val property: KProperty<V>) {
    fun then(block: () -> V) {
        val expectation = Expectation.Getter(property.name)
        val stub = BlockingStub(expectation) { block() }
        mock.addBlockingStub(stub)
    }
}