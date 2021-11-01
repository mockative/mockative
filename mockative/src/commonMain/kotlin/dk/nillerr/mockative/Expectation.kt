package dk.nillerr.mockative

interface Expectation {
    val instance: Any
    var invocation: Invocation
    var result: ExpectationResult?
    var invocations: Int

    fun verify() {
        if (invocations == 0) {
            throw ExpectationNotMetError(instance, invocation)
        }
    }
}