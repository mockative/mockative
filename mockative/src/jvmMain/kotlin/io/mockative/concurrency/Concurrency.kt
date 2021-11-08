package io.mockative.concurrency

/**
 * Since Java doesn't freeze objects crossing thread, [confined] simply calls through to the
 * [block].
 */
internal actual fun <R> confined(block: () -> R): R = block()
