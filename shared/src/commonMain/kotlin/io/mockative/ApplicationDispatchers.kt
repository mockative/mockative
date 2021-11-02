package io.mockative

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class ApplicationDispatchers(val default: CoroutineDispatcher) {
    companion object {
        val Default = ApplicationDispatchers(Dispatchers.Default)
        val Unconfined = ApplicationDispatchers(Dispatchers.Unconfined)
    }
}