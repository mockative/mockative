package io.mockative.concurrency

import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlin.native.concurrent.SharedImmutable

@SharedImmutable
private val synchronizedContext = newSingleThreadContext("synchronized")

internal actual fun <R> confined(block: () -> R): R = runBlocking(synchronizedContext) { block() }
