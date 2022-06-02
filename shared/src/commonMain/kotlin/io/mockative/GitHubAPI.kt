package io.mockative

typealias MyList<T> = List<T>

internal interface GitHubAPI {
    suspend fun create(repository: Repository)

    suspend fun repositories(): MyList<Repository>

    suspend fun repository(id: String): Repository?

    fun thing(with: String, three: Int, arguments: Repository)

    fun String.doStuffToString(): String

    fun getEvent(list: List<*>)

    fun doEvents(list: MyList<Repository>)
}