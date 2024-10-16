package io.github

import io.mockative.Mockable

@Mockable
interface GitHubConfiguration {
    var token: String
}
