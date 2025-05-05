package io.mockative

sealed class InvocationResult {
    class Exception(val exception: Throwable) : io.mockative.InvocationResult() {
        override fun toString(): String {
            return "throw ${exception.getClassName()}() $exception"
        }
    }

    class Return(val value: Any?) : io.mockative.InvocationResult() {
        override fun toString(): String {
            return "$value"
        }
    }
}
