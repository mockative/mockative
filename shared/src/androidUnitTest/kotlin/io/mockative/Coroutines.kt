package io.mockative

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

val newThreadDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
