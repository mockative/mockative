package io.github

@Mockable
class SpyClass(val name: String) {
    fun greet(): String {
        return "Hello, $name"
    }

    fun functionWithManyArgumented(
        string: String,
        int: Int,
        long: Long,
        list: List<String>
    ): String {
        return "$string $int $long $list"
    }
}
