package io.mockative

import kotlinx.coroutines.withContext

internal class GitHubService(
    private val api: GitHubAPI,
    private val configuration: GitHubConfiguration,
    private val dispatchers: ApplicationDispatchers
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

    interface NestedAPI {
        fun foo(): String
    }
}
