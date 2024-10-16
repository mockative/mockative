package io.mockative

/**
 * Executes the given block and records the invocation on any mock object created within the block.
 *
 * @param block the block to be executed
 * @return a [ResultBuilder] object, which allows further configuration of the recorded invocation.
 */
fun <R> every(block: () -> R): ResultBuilder<R> {
    try {
        val (mock, invocation) = MockState.record(block)
        val expectation = invocation.toExpectation()
        return ResultBuilder(mock, expectation)
    } finally {
        Matchers.clear()
    }
}

/**
 * Executes the given block and records the invocation on any mock object created within the block.
 *
 * @param block the block to be executed
 * @return a [SuspendResultBuilder] object, which allows further configuration of the recorded
 * invocation.
 */
suspend fun <R> coEvery(block: suspend () -> R): SuspendResultBuilder<R> {
    try {
        val (mock, invocation) = MockState.record(block)
        val expectation = invocation.toExpectation()
        return SuspendResultBuilder(mock, expectation)
    } finally {
        Matchers.clear()
    }
}
