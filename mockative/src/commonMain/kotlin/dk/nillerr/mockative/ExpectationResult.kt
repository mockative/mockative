package dk.nillerr.mockative

sealed class ExpectationResult {
    data class Constant(val value: Any?) : ExpectationResult()
    data class Immediate(val block: (args: Array<out Any?>) -> Any?) : ExpectationResult()
    data class Suspended(val block: suspend (args: Array<out Any?>) -> Any?) : ExpectationResult()
}