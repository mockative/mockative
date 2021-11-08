package io.mockative

import io.mockative.concurrency.Confined
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.native.concurrent.SharedImmutable

@SharedImmutable
private val givenScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())

internal fun <T : Any, R> record(receiver: T, invocation: T.() -> R, continuation: (MockingInProgressError) -> R) {
    try {
        invocation(receiver)
    } catch (ex: MockingInProgressError) {
        continuation(ex)
    } catch (ex: Throwable) {
        println("Failed to intercept invocation of method on mock: ${ex.message}")
        ex.printStackTrace()
        throw ex
    }
}

internal suspend fun <T : Any, R> record(receiver: T, invocation: suspend T.() -> R, continuation: (MockingInProgressError) -> R) {
    try {
        invocation(receiver)
    } catch (ex: MockingInProgressError) {
        continuation(ex)
    } catch (ex: Throwable) {
        println("Failed to intercept invocation of method on mock: ${ex.message}")
        ex.printStackTrace()
        throw ex
    }
}

internal suspend fun <T : Any, R> recordSuspend(receiver: T, invocation: suspend T.() -> R, continuation: (MockingInProgressError) -> R) {
    try {
        invocation(receiver)
    } catch (ex: MockingInProgressError) {
        continuation(ex)
    } catch (ex: Throwable) {
        println("Failed to intercept invocation of method on mock: ${ex.message}")
        ex.printStackTrace()
        throw ex
    }
}

/**
 * Stubs the invocation of a member on a mock.
 *
 * @param receiver the mock to stub a member of.
 * @param block the block containing the invocation of the member to mock.
 * @param T the type being mocked.
 * @param R the return type of the member being mocked.
 *
 * @throws VerifyNonMockError the [receiver] was not a generated mock instance.
 */
@Suppress("UNCHECKED_CAST")
fun <T : Any, R> given2(receiver: T, block: T.() -> R): ExpectationBuilder<T, R> {
    val mock = receiver as? Mocked<T> ?: throw GivenNonMockError(receiver)

    val builder = ExpectationBuilder<T, R>(receiver)

    mock.data { expectation = builder }

    record(receiver, block) {
        val existingExpectation = it.existingExpectation
        if (existingExpectation != null) {
            mock.data { expectations.remove(existingExpectation) }
        }

        builder.invocation = it.invocation

        mock.data {
            expectation = null
            expectations.add(builder)
        }
    }

    return builder
}

/**
 * Stubs the invocation of a member on a mock.
 *
 * @param receiver the mock to stub a member of.
 * @param block the block containing the invocation of the member to mock.
 * @param T the type being mocked.
 * @param R the return type of the member being mocked.
 *
 * @throws VerifyNonMockError the [receiver] was not a generated mock instance.
 */
@Suppress("UNCHECKED_CAST")
suspend fun <T : Any, R> given(receiver: T, block: suspend T.() -> R): ExpectationBuilder<T, R> {
    val mock = receiver as? Mocked<T> ?: throw GivenNonMockError(receiver)

    val builder = ExpectationBuilder<T, R>(receiver)

    mock.data { expectation = builder }

    record(receiver, block) {
        val existingExpectation = it.existingExpectation
        if (existingExpectation != null) {
            mock.data { expectations.remove(existingExpectation) }
        }

        builder.invocation = it.invocation

        mock.data {
            expectation = null
            expectations.add(builder)
        }
    }

    return builder
}

/**
 * Verifies all expectations on a mock was met at least once.
 *
 * @param receiver the mock to stub a member of.
 * @param T the type being mocked.
 *
 * @throws VerifyNonMockError the [receiver] was not a generated mock instance.
 * @throws ExpectationNotMetError an expectation on the mock was not met.
 */
fun <T : Any> verify(receiver: T) {
    val mock = receiver as? Mocked<*> ?: throw VerifyNonMockError(receiver)
    mock.data { expectations }.forEach { it.verify() }
}

fun <T : Any> close(receiver: T) {
    val mock = receiver as? Mocked<*> ?: throw VerifyNonMockError(receiver)
    mock.data { expectations }.forEach { it.close() }
    mock.data.close()

    mocks { remove(mock) }
}

@SharedImmutable
internal val mocks by lazy { Confined { mutableListOf<Mocked<*>>() } }

/**
 * Validates all mocks created by calling [verify] and [close] on them.
 */
fun validateMocks() {
    mocks { toList() }.run {
        forEach { verify(it) }
        forEach { close(it) }
    }
}

private fun <T : Any> Expectation<T>.verify(exactly: Int? = null, atLeast: Int? = null, atMost: Int? = null) {
    if (exactly == null && atLeast == null && atMost == null && invocations == 0) {
        throw ExpectationNotMetError(instance, invocation)
    }

    if (exactly != null && invocations != exactly) {
        throw ExpectationNotMetError(instance, invocation)
    }

    if (atLeast != null && invocations < atLeast) {
        throw ExpectationNotMetError(instance, invocation)
    }

    if (atMost != null && invocations > atMost) {
        throw ExpectationNotMetError(instance, invocation)
    }
}

fun <T : Any, R> verify(exactly: Int? = null, atLeast: Int? = null, atMost: Int? = null, receiver: T, block: T.() -> R) {
    val mock = receiver as? Mocked<*> ?: throw VerifyNonMockError(receiver)

    record(receiver, block) {
        val expectation = mock.findExpectation(it.invocation)
        expectation.verify(atLeast, atMost, exactly)
    }
}

suspend fun <T : Any, R> verify(exactly: Int? = null, atLeast: Int? = null, atMost: Int? = null, receiver: T, block: suspend T.() -> R) {
    val mock = receiver as? Mocked<*> ?: throw VerifyNonMockError(receiver)

    recordSuspend(receiver, block) {
        val expectation = mock.findExpectation(it.invocation)
        expectation.verify(atLeast, atMost, exactly)
    }
}
//
//interface MockService {
//    var writeable: String
//    val readable: String
//
//    fun doStuff3(intValue: Int, stringValue: String, anyValue: Any)
//    fun doStuff4(intValue: Int, stringValue: String, anyValue: Any, otherStringValue: String)
//
//    fun doStuff(intValue: Int, stringValue: String, anyValue: Any)
//    fun doStuff(intValue: Int, stringValue: String, anyValue: Any, otherStringValue: String)
//
//    suspend fun doStuffLater(intValue: Int, stringValue: String, anyValue: Any)
//}
//
//fun <M, V> given(mock: M, set: M.() -> KMutableProperty0<V>): KGivenMutableProperty<M, V> {
//    TODO()
//}
//
//class KGivenMutableProperty<M, V> {
//    fun whenSetWith(value: Matcher<V>): KGivenMutableProperty<M, V> {
//        TODO()
//    }
//}
//
//fun <M, V> given(mock: M, get: M.() -> KProperty0<V>): KGivenProperty0<M, V> {
//    TODO()
//}
//
//class KGivenProperty0<M, V> {
//    fun thenReturn(value: V) {
//        TODO()
//    }
//}
//
//fun <M, V> given(mock: M, callTo: KMutableProperty0<V>): KGiven4<M, Any, Any, Any, Any, V> {
//    TODO()
//}
//
//fun <M, F, R> given(mock: M, callTo: F): KGiven4<M, Any, Any, Any, Any, R>
//    where F : () -> R, F : KCallable<R> {
//    TODO()
//}
//
//fun <M, F, P1, R> given(mock: M, callTo: F): KGiven4<M, P1, Any, Any, Any, R>
//    where F : (P1) -> R, F : KCallable<R> {
//    TODO()
//}
//
//fun <M, F, P1, P2, P3, R> given(mock: M, callTo: F): KGiven4<M, P1, P2, P3, Any, R>
//    where F : (P1, P2, P3) -> R, F : KCallable<R> {
//    TODO()
//}
//
//fun <M, F, P1, P2, P3, P4, R> given(mock: M, callTo: F): KGiven4<M, P1, P2, P3, P4, R>
//        where F : (P1, P2, P3, P4) -> R, F : KCallable<R>{
//    TODO()
//}
//
//interface Matcher<T>
//
//class KGiven4<M, P1, P2, P3, P4, R> {
//    fun whenCalledWith(p1: Matcher<P1>, p2: Matcher<P2>, p3: Matcher<P3>, p4: Matcher<P4>): KGiven4<M, P1, P2, P3, P4, R> {
//        TODO()
//    }
//
//    fun thenReturn(value: R) {
//        TODO()
//    }
//}
//
//fun <T> anyOf(vararg values: T): Matcher<T> {
//    TODO()
//}
//
//fun <T> eq(value: T): Matcher<T> {
//    TODO()
//}
//
//fun <T> any(): Matcher<T> {
//    TODO()
//}
//
//fun <T> same(value: T): Matcher<T> {
//    TODO()
//}
//
//fun <T> matching(predicate: (T) -> Boolean): Matcher<T> {
//    TODO()
//}
//
//fun <M, V> given2(callTo: KMutableProperty1<M, V>): KGiven4<M, Any, Any, Any, Any, V> {
//    TODO()
//}
//
//fun <M, V> given(mock: M, setter: (M) -> KMutableProperty<V>): KGivenMutableProperty<M, V> = TODO()
//
//fun foo(mockService: MockService) {
//    given(mockService, set = { this::writeable })
//        .whenSetWith(any())
//
//    given(mockService, get = { this::writeable })
//        .thenReturn("abc")
//
//    given(mockService, get = { this::readable })
//        .thenReturn("abc")
//
//    given(mockService, mockService::readable)
//        .thenReturn("abc")
//
//    given(mockService, mockService::doStuff4)
//        .whenCalledWith(anyOf(1, 2,5 ), eq("abc"), any(), matching { true })
//        .thenReturn(Unit)
//}
//
//fun <T : Any> verifyNoMoreInteractions(mock: T) {
//
//}
