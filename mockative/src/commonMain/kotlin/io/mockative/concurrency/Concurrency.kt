package io.mockative.concurrency

/**
 * Ensures invocations of [block]s is confined to the same thread, thus avoiding object freezing in
 * Kotlin/Native due to objects crossing thread boundaries.
 *
 * In Kotlin/JS and Kotlin/JVM, the implementation simply calls through to the [block], since
 * objects aren't frozen when crossing thread boundaries in these targets.
 *
 * Be cautious of nesting calls to [confined], and thus access to [Confined] values, as it
 * can cause segmentation faults in Kotlin/Native.
 */
internal expect fun <R> confined(block: () -> R): R
