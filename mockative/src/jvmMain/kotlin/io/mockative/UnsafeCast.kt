package io.mockative

import net.bytebuddy.ByteBuddy
import java.lang.reflect.Proxy
import kotlin.reflect.KClass

@Suppress("FunctionName")
private fun Class<*>._isSealed(): Boolean {
    return 0 != modifiers and 1 shl 62
}

internal actual fun unsafeCast(value: Any?, type: KClass<*>): Any? {
    val dynamicType = ByteBuddy()
        .subclass(type.java)
        .make()
        .load(type.java.classLoader)
        .loaded

    return dynamicType.constructors.first { it.parameters.isEmpty() }.newInstance()
}

internal actual fun isMatcher(value: Any?): Boolean {
    return value != null && Proxy.isProxyClass(value::class.javaObjectType)
}
