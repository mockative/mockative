package io.mockative

import kotlinx.coroutines.CoroutineScope

expect fun runBlockingTest(block: suspend CoroutineScope.() -> Unit)

/**
 * Dispatches the [block] to a new thread or deferred invocation, blocking until it returns.
 */
expect fun dispatchBlockingTest(block: suspend CoroutineScope.() -> Unit)
