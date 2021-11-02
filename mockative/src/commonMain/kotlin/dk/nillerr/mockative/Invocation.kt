package dk.nillerr.mockative

/**
 * Represents an invocation of a member on a mock.
 *
 * @param method the name of the invoked member.
 * @param arguments the arguments passed during invocation of the member.
 */
data class Invocation(val method: String, val arguments: List<Any?>) {
    internal fun matches(invocation: Invocation): Boolean {
        if (method != invocation.method) {
            return false
        }

        if (!arguments.containsAll(invocation.arguments)) {
            return false
        }

        if (!invocation.arguments.containsAll(arguments)) {
            return false
        }

        return true
    }

    override fun toString(): String {
        return when {
            method.startsWith("\$set_") -> toSetterString()
            method.startsWith("\$get_") -> toGetterString()
            else -> toFunctionString()
        }
    }

    private fun toSetterString(): String {
        return "${method.removePrefix("\$set_")} = ${arguments.single().toString()}"
    }

    private fun toGetterString(): String {
        return method.removePrefix("\$get_")
    }

    private fun toFunctionString(): String {
        return "$method(${arguments.joinToString(", ") { it.toString() }})"
    }
}