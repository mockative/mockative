package io.mockative.fake

import io.mockative.ValueCreationNotSupportedException
import kotlin.reflect.KClass

internal actual fun <T> makeValueOf(type: KClass<*>): T {
    throw ValueCreationNotSupportedException(type)
}
