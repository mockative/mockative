package io.github

import io.mockative.Mockable
import kotlinx.datetime.Clock

@Mockable(Clock::class)
interface GitHubConfiguration {
    var token: String
}
