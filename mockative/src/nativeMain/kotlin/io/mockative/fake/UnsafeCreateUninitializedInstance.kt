@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "ERROR_SUPPRESSION")

package io.mockative.fake

import io.mockative.InternalMockativeApi
import kotlin.native.internal.InternalForKotlinNative
import kotlin.native.internal.createUninitializedInstance

/**
 * Creates an uninitialized instance of [T] on Kotlin/Native using
 * [createUninitializedInstance].
 *
 * **Do not use this function directly.** It exists solely for Mockative's KSP-generated
 * `makeValueOf` implementation and bypasses all constructor logic, leaving the object in
 * an undefined state. Any use outside of generated matcher code is unsupported and unsafe.
 */
@InternalMockativeApi
@OptIn(InternalForKotlinNative::class)
inline fun <reified T> unsafeCreateUninitializedInstance(): T {
    return createUninitializedInstance()
}
