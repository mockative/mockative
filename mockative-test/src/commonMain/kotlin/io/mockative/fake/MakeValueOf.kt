package io.mockative.fake

import kotlin.reflect.KClass

internal expect fun <T> makeValueOf(type: KClass<*>): T
