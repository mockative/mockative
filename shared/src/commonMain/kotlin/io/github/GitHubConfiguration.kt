package io.github

import io.mockative.Mockable
import kotlin.time.Clock

@Mockable(Clock::class, GitHubConfiguration::class)
interface GitHubConfiguration {
    var token: String
}
