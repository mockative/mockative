package io.github

@MockativeMockable
class SpyClass(val name: String) {
    fun test(): String {
        return "Hello, $name"
    }
}