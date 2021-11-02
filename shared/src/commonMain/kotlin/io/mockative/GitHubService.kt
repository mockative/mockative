package io.mockative

import kotlinx.coroutines.withContext

class GitHubService(
    private val api: GitHubAPI,
    private val dispatchers: ApplicationDispatchers
) {
    suspend fun repository(id: String): Repository? {
        return withContext(dispatchers.default) {
            api.repository(id)
        }
    }
}