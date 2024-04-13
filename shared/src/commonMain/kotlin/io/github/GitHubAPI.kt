package io.github

typealias MyList<T> = List<T>

typealias MyStringMap<T> = Map<String, T>

internal interface GitHubAPI {
    var String.wtf: Int

    suspend fun create(repository: Repository)

    suspend fun repositories(): MyList<Repository>

    suspend fun repository(id: String): Repository?

    fun thing(with: String, three: Int, arguments: Repository)

    fun String.doStuffToString(): String

    fun getEvent(list: List<*>)

    fun getObject(`object`: String)

    fun doList(list: MyList<Repository>)

    fun doMaps(map: MyStringMap<Repository>)

    fun count(vararg strings: String): Int
}
