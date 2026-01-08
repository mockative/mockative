package io.mockative

/**
 * This is used to circumvent infinite recursion caused by using the mock instance as a key in as [HashMap].
 */
internal class ByRef(val value: Any) {
    override fun hashCode(): Int {
        return value::class.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is ByRef && value === other.value
    }
}
