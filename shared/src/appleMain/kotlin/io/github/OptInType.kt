package io.github

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class, ExperimentalStdlibApi::class, ExperimentalUnsignedTypes::class)
interface OptInType {
    fun foo(arg: CPointer<*>?)
}
