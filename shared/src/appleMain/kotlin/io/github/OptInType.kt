package io.github

import io.mockative.Mockable
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi

@Mockable
@OptIn(ExperimentalForeignApi::class, ExperimentalStdlibApi::class, ExperimentalUnsignedTypes::class)
interface OptInType {
    fun foo(arg: CPointer<*>?)
}
