package io.mockative

import kotlin.reflect.KClass

internal val KClass<*>.name
    get() = simpleName ?: "KClass[${this::class.hashCode()}]"

internal fun Any.getClassName() = this::class.name

internal fun Any.getPropertyName() = getClassName().let { it[0].lowercase() + it.substring(1) }
