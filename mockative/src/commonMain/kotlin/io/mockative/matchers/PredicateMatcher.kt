package io.mockative.matchers

import io.mockative.name
import kotlin.reflect.KClass

class PredicateMatcher<T>(val type: KClass<*>, override val placeholder: T, val predicate: (T) -> Boolean) : Matcher<T> {
    @Suppress("UNCHECKED_CAST")
    override fun matches(value: Any?): Boolean {
        return type.isInstance(value) && predicate(value as T)
    }

    override fun toString(): String {
        return "matches<${type.name}>"
    }
}
