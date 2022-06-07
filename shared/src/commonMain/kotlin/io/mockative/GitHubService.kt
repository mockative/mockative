package io.mockative

import kotlinx.coroutines.withContext

internal class GitHubService(
    private val api: GitHubAPI,
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

    interface NestedAPI {
        fun foo(): String
    }
}
