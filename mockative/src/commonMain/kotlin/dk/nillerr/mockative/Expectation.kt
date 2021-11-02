package dk.nillerr.mockative

interface Expectation<T : Any> {
    val instance: T
    var invocation: Invocation
    var result: ExpectationResult<T>?
    var invocations: Int

    fun verify() {
        if (invocations == 0) {
            throw ExpectationNotMetError(instance, invocation)
        }
    }
}