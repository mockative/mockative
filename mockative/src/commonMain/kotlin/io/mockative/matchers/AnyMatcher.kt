package io.mockative.matchers

import io.mockative.name
import kotlin.reflect.KClass

class AnyMatcher<T>(private val type: KClass<*>) : Matcher<T> {
    override fun matches(value: Any?): Boolean {
        return type.isInstance(value)
    }

    override fun toString(): String {
        return "any<${type.name}>"
    }
}