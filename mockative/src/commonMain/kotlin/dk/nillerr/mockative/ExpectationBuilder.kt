package dk.nillerr.mockative

class ExpectationBuilder<R>(override val instance: Any) : Expectation {
    override lateinit var invocation: Invocation

    override var result: ExpectationResult? = null
    override var invocations: Int = 0

    fun thenReturn(value: R) {
        result = ExpectationResult.Constant(value)
    }

    fun then(block: (args: Array<out Any?>) -> R) {
        result = ExpectationResult.Immediate(block)
    }

    fun thenSuspend(block: suspend (args: Array<out Any?>) -> R) {
        result = ExpectationResult.Suspended(block)
    }

    fun thenThrow(error: Throwable) = then { throw error }
}

fun ExpectationBuilder<Unit>.thenDoNothing() = thenReturn(Unit)
