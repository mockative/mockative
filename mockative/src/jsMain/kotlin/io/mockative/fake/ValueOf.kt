package io.mockative.fake

import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
internal actual fun <T> makeValueOf(type: KClass<*>): T {
    return Unit as T
}
