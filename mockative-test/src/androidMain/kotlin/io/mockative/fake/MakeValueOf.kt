@file:JvmName("ValueOfJVM")

package io.mockative.fake

import javassist.util.proxy.ProxyFactory
import org.objenesis.ObjenesisStd
import java.lang.reflect.Array
import java.lang.reflect.Modifier
import java.lang.reflect.Proxy
import kotlin.reflect.KClass

private val objenesis = ObjenesisStd()

private fun isSealed(clazz: Class<*>): Boolean {
    return clazz.kotlin.isSealed
}

private fun getPermittedSubclasses(clazz: Class<*>): List<Class<*>> {
    return clazz.kotlin.sealedSubclasses.map { it.java }
}

@Suppress("UNCHECKED_CAST")
internal actual fun <T> makeValueOf(type: KClass<*>): T {
    return makeValueOf(type.java) as T
}

internal fun makeValueOf(clazz: Class<*>): Any? {
    return when {
        clazz.isArray -> Array.newInstance(clazz.componentType, 0)

        isSealed(clazz) -> getPermittedSubclasses(clazz)
            .firstNotNullOf { runCatching { makeValueOf(it) }.getOrNull() }

        clazz.isInterface ->
            Proxy.newProxyInstance(clazz.classLoader, arrayOf(clazz)) { _, method, _ ->
                throw NotImplementedError(method.toString())
            }

        Modifier.isAbstract(clazz.modifiers) -> {
            val constructor = clazz.constructors[0]
            val arguments = constructor.parameterTypes
                .map { valueOf<Any?>(it.kotlin) }
                .toTypedArray()

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
