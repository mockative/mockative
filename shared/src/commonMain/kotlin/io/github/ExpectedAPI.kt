package io.github

import io.mockative.Mockable

@Mockable
expect interface ExpectedAPI {
    fun expectedFunction(): String
}
