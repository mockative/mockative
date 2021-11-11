package io.mockative

import io.mockative.matchers.Matcher
import kotlin.reflect.KProperty

fun <V, P> whenSetting(instance: Any, setter: P): WhenSettingBuilder<V> where P : KProperty<V> {
    return WhenSettingBuilder(instance.asMock(), setter)
}

class WhenSettingBuilder<V>(private val mock: Mockable, private val property: KProperty<V>) {
    fun to(value: Matcher<V>): ResultBuilder {
        return ResultBuilder(value)
    }

    inner class ResultBuilder(private val value: Matcher<*>) {
        fun then(block: (V) -> Any?) {
            val expectation = Expectation.Setter(property.name, value)
            val stub = BlockingStub(expectation) { args ->
                block(args[0] as V)
            }
            mock.addBlockingStub(stub)
        }
    }
}