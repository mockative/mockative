package dk.nillerr.mockative

class GitHubService(private val api: GitHubAPI) {
    suspend fun repository(id: String): Repository? {
        return api.repository(id)
    }
}