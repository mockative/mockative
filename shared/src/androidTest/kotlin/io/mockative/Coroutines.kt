package io.mockative

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

actual fun runBlockingTest(block: suspend CoroutineScope.() -> Unit) =
    runBlocking { block() }

val newThreadDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

actual fun dispatchBlockingTest(block: suspend CoroutineScope.() -> Unit) =
    runBlocking(newThreadDispatcher) { block() }
