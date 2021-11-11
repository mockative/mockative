package io.mockative

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

abstract class Mockable {

    private class StubbingInProgressError(val invocation: Invocation) : Error()

    private val unconfinedScope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)

    private var blockingStubs: List<BlockingStub> by atomic(emptyList())
    private var suspendStubs: List<SuspendStub> by atomic(emptyList())

    private var isRecording: Boolean by atomic(false)

    internal fun addBlockingStub(stub: BlockingStub) {
        blockingStubs = blockingStubs + stub
    }

    private fun getBlockingStub(invocation: Invocation): BlockingStub {
        return blockingStubs.firstOrNull { stub -> stub.expectation.matches(invocation) } ?: throw MissingExpectationError(this, invocation)
    }

    internal fun addSuspendStub(stub: SuspendStub) {
        suspendStubs = suspendStubs + stub
    }

    private fun getSuspendStub(invocation: Invocation): SuspendStub {
        return suspendStubs.firstOrNull { stub -> stub.expectation.matches(invocation) } ?: throw MissingExpectationError(this, invocation)
    }

    /**
     * Records the invocation of a single member on this mock.
     *
     * @param block the block invoking the member on this mock.
     * @return the recorded invocation
     */
    internal fun <T : Any, R> record(block: T.() -> R): Invocation {
        var invocation: Invocation? = null

        isRecording = true

        try {
            block(this as T)
        } catch (error: StubbingInProgressError) {
            invocation = error.invocation
        } finally {
            isRecording = false
        }

        return invocation!!
    }

    /**
     * Records the invocation of a single member on this mock.
     *
     * @param block the block invoking the member on this mock.
     * @return the recorded invocation
     */
    internal fun <T : Any, R> record(block: suspend T.() -> R): Invocation {
        var invocation: Invocation? = null

        unconfinedScope.launch {
            isRecording = true

            try {
                block(this as T)
            } catch (error: StubbingInProgressError) {
                invocation = error.invocation
            } finally {
                isRecording = false
            }
        }

        // The unconfined dispatcher should result in the coroutine running synchronously while recording
        return invocation!!
    }

    internal fun <R> invoke(invocation: Invocation): R {
        if (isRecording) {
            throw StubbingInProgressError(invocation)
        } else {
            val stub = getBlockingStub(invocation)
            val result = stub.invoke(invocation)
            return result as R
        }
    }

    internal suspend fun <R> suspend(invocation: Invocation): R {
        if (isRecording) {
            throw StubbingInProgressError(invocation)
        } else {
            val stub = getSuspendStub(invocation)
            val result = stub.invoke(invocation)
            return result as R
        }
    }

    internal fun reset() {
        blockingStubs = emptyList()
        suspendStubs = emptyList()
    }

    companion object {
        fun <R> invoke(mock: Mockable, invocation: Invocation): R = mock.invoke(invocation)

        suspend fun <R> suspend(mock: Mockable, invocation: Invocation): R = mock.suspend(invocation)
    }
}
