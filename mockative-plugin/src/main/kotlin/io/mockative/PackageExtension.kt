package io.mockative

class PackageExtension(private val prefix: String, private val delegate: MockativeExtension) : MemberScope(prefix, delegate) {
    fun type(vararg simpleNames: String, block: MemberScope.() -> Unit) {
        val nestedPrefix = "$prefix.${simpleNames.joinToString(".")}"
        MemberScope(nestedPrefix, delegate).block()
    }
}
