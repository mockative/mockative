package io.mockative

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking

actual fun runBlockingTest(block: suspend CoroutineScope.() -> Unit) =
    runBlocking { block() }

val newThreadDispatcher = newSingleThreadContext("dispatchBlockingTest")

actual fun dispatchBlockingTest(block: suspend CoroutineScope.() -> Unit) =
    runBlocking(newThreadDispatcher) { block() }