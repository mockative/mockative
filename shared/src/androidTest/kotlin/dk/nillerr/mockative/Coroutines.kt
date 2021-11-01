package dk.nillerr.mockative

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

actual fun runBlockingTest(block: suspend CoroutineScope.() -> Unit) =
    runBlocking { block() }
