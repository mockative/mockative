package io.mockative

interface GitHubAPI {
    suspend fun create(repository: Repository)

    suspend fun repositories(): List<Repository>

    suspend fun repository(id: String): Repository?

    fun thing(with: String, three: Int, arguments: Repository)
}