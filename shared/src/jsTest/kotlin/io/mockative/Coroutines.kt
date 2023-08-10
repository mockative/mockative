package io.mockative

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.promise

private val testScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
