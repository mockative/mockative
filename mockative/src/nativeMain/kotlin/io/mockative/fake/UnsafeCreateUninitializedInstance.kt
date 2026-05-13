@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package io.mockative.fake

@OptIn(kotlin.native.internal.InternalForKotlinNative::class)
inline fun <reified T> unsafeCreateUninitializedInstance(): T {
    return kotlin.native.internal.createUninitializedInstance()
}
