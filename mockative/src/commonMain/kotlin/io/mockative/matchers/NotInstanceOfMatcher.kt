package io.mockative.matchers

import io.mockative.name
import kotlin.reflect.KClass

class NotInstanceOfMatcher<T : Any>(override val placeholder: T, private val type: KClass<T>) : Matcher<T> {
    override fun matches(value: Any?): Boolean {
        return !type.isInstance(value)
    }

    override fun toString(): String {
        return "<value !is ${type.qualifiedName ?: type.simpleName ?: type.name}>"
    }
}
