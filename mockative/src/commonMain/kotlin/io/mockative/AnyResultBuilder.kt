package io.mockative

interface AnyResultBuilder<R> {
    fun invokes(block: () -> R)

    fun returns(value: R) = invokes { value }

    fun throws(throwable: Throwable) = invokes { throw throwable }

    @Deprecated("Replaced by the `invokes` function", replaceWith = ReplaceWith("invokes(value)"))
    fun thenInvoke(block: () -> R) = invokes(block)

    @Deprecated("Replaced by the `returns` function", replaceWith = ReplaceWith("returns(value)"))
    fun thenReturn(value: R) = invokes { value }

    @Deprecated("Replaced by the `throws` function", replaceWith = ReplaceWith("throws"))
    fun thenThrow(throwable: Throwable) = invokes { throw throwable }
}

fun AnyResultBuilder<Unit>.doesNothing() = invokes { }

@Deprecated("Replaced by the `doesNothing` function", replaceWith = ReplaceWith("doesNothing"))
fun AnyResultBuilder<Unit>.thenDoNothing() = invokes { }
