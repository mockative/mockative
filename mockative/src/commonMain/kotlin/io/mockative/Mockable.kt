package io.mockative

import io.mockative.concurrency.AtomicList
import io.mockative.concurrency.AtomicSet
import io.mockative.concurrency.atomic
import kotlin.native.concurrent.ThreadLocal

class Mockable(val instance: Any) {
    // Serves as a workaround for getting default implementations to work with Kotlin/JS
    private val instanceToken = Any()

    private val blockingStubs = AtomicList<BlockingStub>()
    private val suspendStubs = AtomicList<SuspendStub>()
    private val verifiedInvocations = AtomicSet<Invocation>()

    internal var stubsUnitsByDefault: Boolean by atomic(false)
    internal var isSpy: Boolean by atomic(false)

    internal fun reset() {
        blockingStubs.clear()
        suspendStubs.clear()
        verifiedInvocations.clear()
    }

    internal fun unmock() {
        blockingStubs.clear()
        suspendStubs.clear()
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

    private fun getBlockingStub(invocation: Invocation, fallback: ((Array<Any?>) -> Any?)?): BlockingStub? {
        return when (val blockingStub = getBlockingStubOrNull(invocation)) {
            null -> {
                if (isSpy) return null
                when (fallback) {
                    null -> null
                    else -> {
                        val fallbackStub = OpenBlockingStub(invocation.toOpenExpectation(), fallback)
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
            null -> throw MissingExpectationException(instanceToken, invocation, false, expectations)
            else -> throw InvalidExpectationException(instanceToken, invocation, false, expectations)
        }
    }

    private fun getBlockingStubOrNull(invocation: Invocation): BlockingStub? {
        return blockingStubs.firstOrNull { stub -> stub.matches(invocation) }
    }

    internal fun addSuspendStub(stub: SuspendStub) {
        suspendStubs.add(0, stub)
    }

    private val expectations: List<Expectation>
        get() = suspendStubs.map { it.expectation } + blockingStubs.map { it.expectation }

    private fun getSuspendStub(invocation: Invocation, fallback: ((Array<Any?>) -> Any?)?): SuspendStub? {
        return when (val suspendStub = getSuspendStubOrNull(invocation)) {
            null -> {
                if (isSpy) return null
                when (fallback) {
                    null -> null
                    else -> {
                        val fallbackStub = OpenSuspendStub(invocation.toOpenExpectation(), fallback)
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
            null -> throw MissingExpectationException(instance, invocation, true, expectations)
            else -> throw InvalidExpectationException(instance, invocation, true, expectations)
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
     * @throws UnverifiedInvocationsException the mock contains unverified invocations
     */
    internal fun confirmVerified() {
        val unverified = unverifiedInvocations
        if (unverified.isNotEmpty()) {
            throw UnverifiedInvocationsException(this, unverified)
        }
    }

    internal fun verifyNoUnmetExpectations() {
        val unusedBlockingStubs = blockingStubs.filter { it.invocations.isEmpty() }
        val unusedSuspendStubs = suspendStubs.filter { it.invocations.isEmpty() }

        val unmetBlockingExpectations = unusedBlockingStubs.map { it.expectation }
        val unmetSuspendExpectations = unusedSuspendStubs.map { it.expectation }

        val unmetExpectations = unmetBlockingExpectations + unmetSuspendExpectations

        if (unmetExpectations.isNotEmpty()) {
            throw MockValidationException(this, unmetExpectations, invocations)
        }
    }

    @Suppress("UNCHECKED_CAST")
    internal fun <R> invoke(invocation: Invocation, returnsUnit: Boolean, spyBlock: () -> R): R {
        if (isRecording) {
            throw StubbingInProgressException(this, invocation)
        } else {
            val fallback = getUnitFallbackOrNull(returnsUnit)
            val stub = getBlockingStub(invocation, fallback)
            if (stub == null && !isSpy) throwMissingBlockingStubException(invocation)
            else if (stub == null && isSpy) return spyBlock()

            try {
                val result = stub!!.invoke(invocation)
                invocation.result = InvocationResult.Return(result)
                return result as R
            } catch (e: Throwable) {
                invocation.result = InvocationResult.Exception(e)
                throw e
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    internal fun <R> invoke(invocation: Invocation, default: () -> R, spyBlock: () -> R): R {
        if (isRecording) {
            throw StubbingInProgressException(this, invocation)
        } else {
            return when (val stub = getBlockingStubOrNull(invocation)) {
                null -> if (isSpy) spyBlock() else default()
                else -> stub.invoke(invocation) as R
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    internal suspend fun <R> suspend(invocation: Invocation, returnsUnit: Boolean, spyBlock: suspend () -> R): R {
        if (isRecording) {
            throw StubbingInProgressException(this, invocation)
        } else {
            val fallback = getUnitFallbackOrNull(returnsUnit)
            val stub = getSuspendStub(invocation, fallback)
            if (stub == null && !isSpy) throwMissingSuspendStubException(invocation)
            else if (stub == null && isSpy) return spyBlock()

            try {
                val result = stub!!.invoke(invocation)
                invocation.result = InvocationResult.Return(result)
                return result as R
            } catch (e: Throwable) {
                invocation.result = InvocationResult.Exception(e)
                throw e
            }
        }
    }

    fun debug() {
        val debugString = buildString {
            appendLine(this@Mockable.getClassName())
            appendLine("-----")
            appendLine("  Blocking Stubs")

            val blockingStubs = blockingStubs.reversed()
            if (blockingStubs.isEmpty()) {
                appendLine("    <none>")
            } else {
                blockingStubs.forEach { stub ->
                    appendLine("    ${stub.expectation}")
                    for (invocation in stub.invocations) {
                        val indicator = if (verifiedInvocations.contains(invocation)) "✓" else "?"
                        appendLine("$indicator $invocation".prependIndent("      "))
                    }
                }
            }

            appendLine("-----")
            appendLine("  Suspend Stubs")

            val suspendStubs = suspendStubs.reversed()
            if (suspendStubs.isEmpty()) {
                appendLine("    <none>")
            } else {
                suspendStubs.forEach { stub ->
                    appendLine("    ${stub.expectation}")
                    for (invocation in stub.invocations) {
                        val indicator = if (verifiedInvocations.contains(invocation)) "✓" else "?"
                        appendLine("$indicator $invocation".prependIndent("      "))
                    }
                }
            }

            appendLine("-----")

            appendLine("  Invocations")
            val invocations = invocations
            if (invocations.isEmpty()) {
                appendLine("    <none>")
            } else {
                invocations.forEach {
                    val invocation = it.toString()
                    val prefix = if (verifiedInvocations.contains(it)) "✓" else "?"
                    appendLine("$prefix $invocation".prependIndent("    "))
                }
            }
            appendLine("-----")
        }

        println(debugString)
    }

    @ThreadLocal
    companion object {
        var isRecording: Boolean = false

        private val mockables = mutableMapOf<Any, Mockable>()

        internal fun mockable(instance: Any): Mockable {
            if (!isMock(instance)) {
                throw ReceiverNotMockedException(instance)
            }

            return mockables.getOrPut(instance) { Mockable(instance) }
        }

        fun <R> invoke(instance: Any, invocation: Invocation, returnsUnit: Boolean, spyBlock: () -> R): R {
            return mockable(instance).invoke(invocation, returnsUnit, spyBlock)
        }

        fun <R> invoke(instance: Any, invocation: Invocation, default: () -> R, spyBlock: () -> R): R {
            return mockable(instance).invoke(invocation, default, spyBlock)
        }

        suspend fun <R> suspend(instance: Any, invocation: Invocation, returnsUnit: Boolean, spyBlock: suspend () -> R): R {
            return mockable(instance).suspend(invocation, returnsUnit, spyBlock)
        }

        /**
         * Records the invocation of a single member on this mock.
         *
         * @param block the block invoking the member on this mock.
         * @return the recorded invocation
         */
        @Suppress("DuplicatedCode")
        fun <R> record(block: () -> R): Pair<Mockable, Invocation> {
            var receiver: Mockable? = null
            var invocation: Invocation? = null

            isRecording = true

            try {
                block()
            } catch (error: StubbingInProgressException) {
                receiver = error.receiver
                invocation = error.invocation
            } finally {
                isRecording = false
            }

            return receiver!! to invocation!!
        }

        /**
         * Records the invocation of a single member on this mock.
         *
         * @param block the block invoking the member on this mock.
         * @return the recorded invocation
         */
        @Suppress("DuplicatedCode")
        suspend fun <R> record(block: suspend () -> R): Pair<Mockable, Invocation> {
            var receiver: Mockable? = null
            var invocation: Invocation? = null

            isRecording = true

            try {
                block()
            } catch (error: StubbingInProgressException) {
                receiver = error.receiver
                invocation = error.invocation
            } finally {
                isRecording = false
            }

            return receiver!! to invocation!!
        }

        /**
         * Prints a representation of the internal state of the [target] mock, useful while debugging issues.
         *
         * The debug message is formatted like this:
         *
         * ```
         * <name of mock>
         * -----
         *   Blocking Stubs
         *     <list of blocking stubs (if any) and their invocations (if any)>
         * -----
         *   Suspend Stubs
         *     <list of coroutine stubs (if any) and their invocations (if any)>
         * -----
         *   Invocations
         *     <list of all invocations (if any)>
         * -----
         * ```
         *
         * In front of every logged invocation is either a checkmark (✓) meaning the invocation has been verified, or a
         * question-mark (?) meaning the invocation has not been verified.
         *
         * Note: The order of the debug output represents the internal state and as such may not always match the
         * actual order of declaration in code.
         *
         * Example:
         * ```
         * GitHubAPIMock
         * -----
         *   Blocking Stubs
         *     <none>
         * -----
         *   Suspend Stubs
         *     repository(0efb1b3b-f1b2-41f8-a1d8-368027cc86ee)
         *       ? repository(0efb1b3b-f1b2-41f8-a1d8-368027cc86ee) =
         *           Repository(id=0efb1b3b-f1b2-41f8-a1d8-368027cc86ee, name=Mockito)
         *     repository(0efb1b3b-f1b2-41f8-a1d8-368027cc86ee)
         *       ✓ repository(0efb1b3b-f1b2-41f8-a1d8-368027cc86ee) =
         *           Repository(id=0efb1b3b-f1b2-41f8-a1d8-368027cc86ee, name=Mockative)
         * -----
         *   Invocations
         *     ? repository(0efb1b3b-f1b2-41f8-a1d8-368027cc86ee) =
         *         Repository(id=0efb1b3b-f1b2-41f8-a1d8-368027cc86ee, name=Mockito)
         *     ✓ repository(0efb1b3b-f1b2-41f8-a1d8-368027cc86ee) =
         *         Repository(id=0efb1b3b-f1b2-41f8-a1d8-368027cc86ee, name=Mockative)
         * -----
         * ```
         */
        fun debug(instance: Any) {
            mockable(instance).debug()
        }
    }
}
