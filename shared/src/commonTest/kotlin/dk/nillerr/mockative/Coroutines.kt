package dk.nillerr.mockative

import kotlinx.coroutines.CoroutineScope

expect fun runBlockingTest(block: suspend CoroutineScope.() -> Unit)
