package io.mockative

import io.mockative.matchers.Matcher
import kotlin.reflect.KClass

internal actual fun unsafeCast(value: Any?, type: KClass<*>): Any? {
    return value
}

internal actual fun isMatcher(value: Any?): Boolean {
    return value is Matcher<*>
}
