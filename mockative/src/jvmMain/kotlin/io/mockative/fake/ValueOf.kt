@file:JvmName("ValueOfJVM")
package io.mockative.fake

import javassist.util.proxy.ProxyFactory
import org.objenesis.ObjenesisStd
import java.lang.reflect.Array
import java.lang.reflect.Modifier
import java.lang.reflect.Proxy
import kotlin.reflect.KClass

private val objenesis = ObjenesisStd()

private val hasSealed by lazy {
    Class::class.java.methods.any { it.name == "isSealed" }
}

@Suppress("UNCHECKED_CAST", "NewApi")
internal actual fun <T> makeValueOf(type: KClass<*>): T {
    return makeValueOf(type.java) as T
}

@Suppress("NewApi")
internal fun makeValueOf(clazz: Class<*>): Any? {
    return when {
        clazz.isArray -> Array.newInstance(clazz.componentType, 0)

        hasSealed && clazz.isSealed -> clazz.permittedSubclasses
            .firstNotNullOf { runCatching { makeValueOf(it) }.getOrNull() }

        clazz.isInterface -> Proxy.newProxyInstance(clazz.classLoader, arrayOf(clazz)) { _, method, _ ->
            error(method.toString())
        }

        Modifier.isAbstract(clazz.modifiers) -> {
            val constructor = clazz.constructors[0]
            val arguments = Array(constructor.parameterCount) { index ->
                valueOf<Any?>(constructor.parameterTypes[index].kotlin)
            }

            val proxyFactory = ProxyFactory()
            proxyFactory.superclass = clazz
            proxyFactory.isUseCache = true
            proxyFactory.create(constructor.parameterTypes, arguments)
        }

        clazz.isAnnotationPresent(JvmInline::class.java) -> {
            val constructor = clazz.declaredConstructors[0]
            val argument = valueOf<Any?>(constructor.parameterTypes[0].kotlin)

            constructor.isAccessible = true
            try {
                return constructor.newInstance(argument)
            } finally {
                constructor.isAccessible = false
            }
        }

        else -> objenesis.newInstance(clazz)
    }
}
