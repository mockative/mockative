package io.mockative

import kotlin.reflect.KClass

internal val KClass<*>.name
    get() = simpleName ?: "KClass[${this::class.hashCode()}]"

internal fun Any.getClassName() = this::class.name

internal fun Any.asMockable(): Mockable = this as? Mockable ?: throw StubbingNonMockError(this)
