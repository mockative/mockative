package io.mockative

open class MemberScope(private val prefix: String, private val delegate: MockativeExtension) {
    fun exclude(memberName: String) {
        val fqn = "$prefix.$memberName"
        delegate.excludeMembers.add(fqn)
    }
}
