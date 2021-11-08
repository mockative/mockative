package io.mockative.concurrency

/**
 * Since JavaScript is single-threaded, [confined] simply calls through to the [block].
 */
internal actual fun <R> confined(block: () -> R): R = block()
