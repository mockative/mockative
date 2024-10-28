package io.github

import io.mockative.ValueCreationNotSupportedException

/**
 * Kotlin/Wasm doesn't support implicitly casting `Unit` as `T` so we ignore any expected errors related to this in
 * tests.
 *
 * We may want to explore supporting Kotlin/Wasm with implicit value generation in the future.
 */
inline fun ignoreKotlinWasm(block: () -> Unit) {
    try {
        block()
    } catch (e: ValueCreationNotSupportedException) {
        // Nothing
    }
}
