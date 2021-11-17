package io.mockative

import io.mockative.concurrency.AtomicList
import io.mockative.concurrency.AtomicSet
import io.mockative.concurrency.atomic
import io.mockative.matchers.AnyArgumentsMatcher

abstract class Mockable {

    private class StubbingInProgressError(val invocation: Invocation) : Error()

    private val blockingStubs = AtomicList<BlockingStub>()
    private val suspendStubs = AtomicList<SuspendStub>()
    private val verifiedInvocations = AtomicSet<Invocation>()

    private var isRecording: Boolean by atomic(false)

    internal fun reset() {
        blockingStubs.clear()
        suspendStubs.clear()
        verifiedInvocations.clear()
    }

    internal fun addBlockingStub(stub: BlockingStub) {
        blockingStubs.add(stub)
    }

    private fun getBlockingStub(invocation: Invocation): BlockingStub {
        return getBlockingStubOrNull(invocation)
            ?: getSuspendStubOrNull(invocation)
                ?.let { throw InvalidExpectationError(this, invocation, false, expectations) }
            ?: throw MissingExpectationError(this, invocation, false, expectations)
    }

    private fun getBlockingStubOrNull(invocation: Invocation): BlockingStub? {
        return blockingStubs.firstOrNull { stub -> stub.expectation.matches(invocation) }
    }

    internal fun addSuspendStub(stub: SuspendStub) {
        suspendStubs.add(stub)
    }

    private val expectations: List<Expectation>
        get() = suspendStubs.map { it.expectation } + blockingStubs.map { it.expectation }

    private fun getSuspendStub(invocation: Invocation): SuspendStub {
        return getSuspendStubOrNull(invocation)
            ?: getBlockingStubOrNull(invocation)
                ?.let { throw InvalidExpectationError(this, invocation, true, expectations) }
            ?: throw MissingExpectationError(this, invocation, true, expectations)
    }

    private fun getSuspendStubOrNull(invocation: Invocation): SuspendStub? {
        return suspendStubs.firstOrNull { stub -> stub.expectation.matches(invocation) }
    }

    private val invocations: List<Invocation>
        get() {
            val blockingInvocations = blockingStubs.flatMap { it.invocations }
            val suspendInvocations = suspendStubs.flatMap { it.invocations }
            return blockingInvocations + suspendInvocations
        }

    private val unverifiedInvocations: List<Invocation>
        get() {
            val verified = verifiedInvocations
            return invocations.filterNot { verified.contains(it) }
        }

    internal fun verify(verifier: Verifier) {
        val unverified = unverifiedInvocations
        val matches = verifier.verify(this, unverified)
        verifiedInvocations.addAll(matches)
    }

    /**
     * @throws UnverifiedInvocationsError the mock contains unverified invocations
     */
    internal fun confirmVerified() {
        val unverified = unverifiedInvocations
        if (unverified.isNotEmpty()) {
            throw UnverifiedInvocationsError(this, unverified)
        }
    }

    internal fun verifyNoUnmetExpectations() {
        val unusedBlockingStubs = blockingStubs.filter { it.invocations.isEmpty() }
        val unusedSuspendStubs = suspendStubs.filter { it.invocations.isEmpty() }

        val unmetBlockingExpectations = unusedBlockingStubs.map { it.expectation }
        val unmetSuspendExpectations = unusedSuspendStubs.map { it.expectation }

        val unmetExpectations = unmetBlockingExpectations + unmetSuspendExpectations

        if (unmetExpectations.isNotEmpty()) {
            throw MockValidationError(this, unmetExpectations, invocations)
        }
    }

    /**
     * Records the invocation of a single member on this mock.
     *
     * @param block the block invoking the member on this mock.
     * @return the recorded invocation
     */
    @Suppress("UNCHECKED_CAST")
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

    @Suppress("UNCHECKED_CAST")
    internal open fun <R> invoke(invocation: Invocation): R {
        if (isRecording) {
            throw StubbingInProgressError(invocation)
        } else {
            val stub = getBlockingStub(invocation)
            val result = stub.invoke(invocation)
            return result as R
        }
    }

    /**
     * Records the invocation of a single member on this mock.
     *
     * @param block the block invoking the member on this mock.
     * @return the recorded invocation
     */
    @Suppress("UNCHECKED_CAST")
    internal suspend fun <T : Any, R> record(block: suspend T.() -> R): Invocation {
        var invocation: Invocation? = null

        isRecording = true

        try {
            block(this as T)
        } catch (error: StubbingInProgressError) {
            invocation = error.invocation
        } finally {
            isRecording = false
        }

        // The unconfined dispatcher should result in the coroutine running synchronously while recording
        return invocation!!
    }

    @Suppress("UNCHECKED_CAST")
    internal suspend fun <R> suspend(invocation: Invocation): R {
        if (isRecording) {
            throw StubbingInProgressError(invocation)
        } else {
            val stub = getSuspendStub(invocation)
            val result = stub.invoke(invocation)
            return result as R
        }
    }

    companion object {
        fun <R> invoke(mock: Mockable, invocation: Invocation): R = mock.invoke(invocation)

        suspend fun <R> suspend(mock: Mockable, invocation: Invocation): R = mock.suspend(invocation)
    }
}
