package dk.nillerr.mockative

import kotlin.reflect.KClass

internal val KClass<*>.name
    get() = simpleName ?: "KClass[${this::class.hashCode()}]"

internal fun Any.getClassName() = this::class.simpleName ?: "KClass[${this::class.hashCode()}]"

/**
 * The exception expected to be thrown by a method while it is being mocked through a call to
 * [given].
 */
internal class MockingInProgressError : Error()