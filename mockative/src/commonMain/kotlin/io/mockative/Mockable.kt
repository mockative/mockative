package io.mockative

import io.mockative.concurrency.AtomicList
import io.mockative.concurrency.AtomicSet
import io.mockative.concurrency.atomic

abstract class Mockable(stubsUnitByDefault: Boolean) {

    // Serves as a workaround for getting default implementations to work with Kotlin/JS
    private val instance = Any()

    private class StubbingInProgressError(val invocation: Invocation) : Error()

    private val blockingStubs = AtomicList<BlockingStub>()
    private val suspendStubs = AtomicList<SuspendStub>()
    private val verifiedInvocations = AtomicSet<Invocation>()

    private var isRecording: Boolean by atomic(false)

    internal var stubsUnitsByDefault: Boolean by atomic(stubsUnitByDefault)

    internal fun reset() {
        blockingStubs.clear()
        suspendStubs.clear()
        verifiedInvocations.clear()
    }

    internal fun addBlockingStub(stub: BlockingStub) {
        blockingStubs.add(0, stub)
    }

    private fun getUnitFallbackOrNull(returnsUnit: Boolean): ((Array<Any?>) -> Any?)? {
        if (returnsUnit && stubsUnitsByDefault) {
            return { }
        } else {
            return null
        }
    }

    private fun getBlockingStub(invocation: Invocation, fallback: ((Array<Any?>) -> Any?)?): BlockingStub {
        return when (val blockingStub = getBlockingStubOrNull(invocation)) {
            null -> {
                when (fallback) {
                    null -> throwMissingBlockingStubException(invocation)
                    else -> {
                        val fallbackStub = BlockingStub(invocation.toOpenExpectation(), fallback)
                        addBlockingStub(fallbackStub)
                        return fallbackStub
                    }
                }
            }
            else -> blockingStub
        }
    }

    private fun throwMissingBlockingStubException(invocation: Invocation): Nothing {
        when (getSuspendStubOrNull(invocation)) {
            null -> throw MissingExpectationError(this, invocation, false, expectations)
            else -> throw InvalidExpectationError(this, invocation, false, expectations)
        }
    }

    private fun getBlockingStubOrNull(invocation: Invocation): BlockingStub? {
        return blockingStubs.firstOrNull { stub -> stub.expectation.matches(invocation) }
    }

    internal fun addSuspendStub(stub: SuspendStub) {
        suspendStubs.add(0, stub)
    }

    private val expectations: List<Expectation>
        get() = suspendStubs.map { it.expectation } + blockingStubs.map { it.expectation }

    private fun getSuspendStub(invocation: Invocation, fallback: ((Array<Any?>) -> Any?)?): SuspendStub {
        return when (val suspendStub = getSuspendStubOrNull(invocation)) {
            null -> {
                when (fallback) {
                    null -> throwMissingSuspendStubException(invocation)
                    else -> {
                        val fallbackStub = SuspendStub(invocation.toOpenExpectation(), fallback)
                        addSuspendStub(fallbackStub)
                        return fallbackStub
                    }
                }
            }
            else -> suspendStub
        }
    }

    private fun throwMissingSuspendStubException(invocation: Invocation): Nothing {
        when (getBlockingStubOrNull(invocation)) {
            null -> throw MissingExpectationError(this, invocation, true, expectations)
            else -> throw InvalidExpectationError(this, invocation, true, expectations)
        }
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
    @Suppress("UNCHECKED_CAST", "DuplicatedCode")
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
    protected fun <R> invoke(invocation: Invocation, returnsUnit: Boolean): R {
        if (isRecording) {
            throw StubbingInProgressError(invocation)
        } else {
            val fallback = getUnitFallbackOrNull(returnsUnit)
            val stub = getBlockingStub(invocation, fallback)
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
    @Suppress("UNCHECKED_CAST", "DuplicatedCode")
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

        return invocation!!
    }

    @Suppress("UNCHECKED_CAST")
    protected suspend fun <R> suspend(invocation: Invocation, returnsUnit: Boolean): R {
        if (isRecording) {
            throw StubbingInProgressError(invocation)
        } else {
            val fallback = getUnitFallbackOrNull(returnsUnit)
            val stub = getSuspendStub(invocation, fallback)
            val result = stub.invoke(invocation)
            return result as R
        }
    }

    private inline fun <reified R> invokeWithFallback(invocation: Invocation, default: () -> R): R {
        if (isRecording) {
            throw StubbingInProgressError(invocation)
        } else {
            return when (val stub = getBlockingStubOrNull(invocation)) {
                null -> default()
                else -> stub.invoke(invocation) as R
            }
        }
    }

    @Suppress("ReplaceCallWithBinaryOperator")
    override fun equals(other: Any?): Boolean {
        return invokeWithFallback(Invocation.Function("equals", listOf(other))) {
            instance.equals((other as? Mockable)?.instance)
        }
    }

    override fun hashCode(): Int {
        return invokeWithFallback(Invocation.Function("hashCode", emptyList())) {
            instance.hashCode()
        }
    }

    override fun toString(): String {
        return invokeWithFallback(Invocation.Function("toString", emptyList())) {
            "io.mockative.Mockable@${instance.hashCode()}"
        }
    }
}
