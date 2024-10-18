package io.github

import io.mockative.Mockable

@Mockable
actual interface ExpectedAPI {
    actual fun expectedFunction(): String
}
