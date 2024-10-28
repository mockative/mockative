package io.mockative

open class MemberScope(private val prefix: String, private val delegate: MockativeExtension) {
    fun exclude(memberName: String) {
        val fqn = "$prefix.$memberName"
        delegate.excludeMembers.add(fqn)
    }

    fun optIn(annotation: String) {
        optInTo(prefix, annotation)
    }

    fun optIn(memberName: String, annotation: String) {
        optInTo("$prefix.$memberName", annotation)
    }

    private fun optInTo(fqn: String, annotation: String) {
        val optIn = delegate.optIn.get()

        val current = optIn[fqn]
        if (current != null) {
            delegate.optIn.put(fqn, current + annotation)
        } else {
            delegate.optIn.put(fqn, listOf(annotation))
        }
    }
}
