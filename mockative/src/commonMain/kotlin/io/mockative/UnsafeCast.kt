package io.mockative

import kotlin.reflect.KClass

internal expect fun unsafeCast(value: Any?, type: KClass<*>): Any?

internal expect fun isMatcher(value: Any?): Boolean
