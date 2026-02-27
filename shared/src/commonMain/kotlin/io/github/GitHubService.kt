package io.github

import io.mockative.Mockable
import kotlinx.coroutines.withContext
import kotlin.time.Clock

internal class GitHubService(
    private val api: GitHubAPI,
    private val configuration: GitHubConfiguration,
    private val dispatchers: ApplicationDispatchers,
    private val clock: Clock,
) {
    suspend fun create(repository: Repository) {
        return withContext(dispatchers.default) {
            api.create(repository)
        }
    }

    suspend fun repository(id: String): Repository? {
        return withContext(dispatchers.default) {
            api.repository(id)
        }
    }

    fun getToken(): String {
        return configuration.token
    }

    fun setToken(token: String) {
        configuration.token = token
    }

    @Mockable
    interface NestedAPI {
        fun foo(): String
    }
}
