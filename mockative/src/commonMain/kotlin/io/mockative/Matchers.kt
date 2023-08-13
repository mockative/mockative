package io.mockative

import io.mockative.fake.valueOf
import io.mockative.matchers.*
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object Matchers {
    private val matchers = mutableListOf<Matcher<*>>()

    val size: Int
        get() = matchers.size

    fun <T> enqueue(matcher: Matcher<T>): T {
        matchers.add(matcher)
        return matcher.placeholder
    }

    fun dequeue(): Matcher<*> {
        return matchers.removeFirst()
    }

    fun clear() {
        matchers.clear()
    }
}

/**
 * Matches any value
 * @param T The type of the parameter to match
 * @return A generated value of the specified type [T]
 */
inline fun <reified T> any(): T {
    return any(valueOf<T>())
}

/**
 * Matches any value while specifying a valid value for use during stubbing.
 * @param T The type of the parameter to match
 * @param validValue A valid value to use during stubbing. This value is only used to detect the
 * member being stubbed.
 * @return The [validValue]
 */
inline fun <reified T> any(validValue: T): T {
    return Matchers.enqueue(AnyMatcher(validValue))
}

/**
 * Matches any value that is an instance of the specified type [T].
 * @param T The type of value to match
 * @return A generated value of the specified type [T]
 */
inline fun <reified T : Any> instanceOf(): T {
    return instanceOf(valueOf<T>())
}

/**
 * Matches any value that is an instance of the specified type [T], while specifying a valid value
 * for use during stubbing.
 * @param T The type of value to match
 * @param validValue A valid value to use during stubbing. This value is only used to detect the
 * member being stubbed.
 * @return The [validValue]
 */
inline fun <reified T : Any> instanceOf(validValue: T): T {
    return Matchers.enqueue(InstanceOfMatcher(validValue, T::class))
}

/**
 * Matches any value that is an instance of the specified type [T].
 * @param T The type of value to match
 * @return A generated value of the specified type [T]
 */
inline fun <reified T : Any> notInstanceOf(): T {
    return notInstanceOf(valueOf<T>())
}

/**
 * Matches any value that is an instance of the specified type [T], while specifying a valid value
 * for use during stubbing.
 * @param T The type of value to match
 * @param validValue A valid value to use during stubbing. This value is only used to detect the
 * member being stubbed.
 * @return The [validValue]
 */
inline fun <reified T : Any> notInstanceOf(validValue: T): T {
    return Matchers.enqueue(NotInstanceOfMatcher(validValue, T::class))
}

inline fun <reified T> eq(value: T): T {
    return Matchers.enqueue(EqualsMatcher(value))
}

inline fun <reified T> ne(value: T): T {
    return Matchers.enqueue(NotEqualsMatcher(value))
}

inline fun <reified T> oneOf(vararg values: T): T {
    return Matchers.enqueue(OneOfMatcher(values.toList()))
}

inline fun <reified T> `in`(values: Iterable<T>): T {
    return Matchers.enqueue(OneOfMatcher(values.toList()))
}

inline fun <reified T> notOneOf(vararg values: T): T {
    return Matchers.enqueue(NotOneOfMatcher(values.toList()))
}

inline fun <reified T> notIn(values: Iterable<T>): T {
    return Matchers.enqueue(NotOneOfMatcher(values.toList()))
}

inline fun <reified T : Comparable<T>> gt(value: T): T {
    return Matchers.enqueue(ComparableMatcher(T::class, value, ">") { a, b -> a > b })
}

inline fun <reified T : Comparable<T>> gte(value: T): T {
    return Matchers.enqueue(ComparableMatcher(T::class, value, ">=") { a, b -> a >= b })
}

inline fun <reified T : Comparable<T>> lt(value: T): T {
    return Matchers.enqueue(ComparableMatcher(T::class, value, "<") { a, b -> a < b })
}

inline fun <reified T : Comparable<T>> lte(value: T): T {
    return Matchers.enqueue(ComparableMatcher(T::class, value, "<=") { a, b -> a <= b })
}

inline fun <reified T> matches(noinline predicate: (T) -> Boolean): T {
    return matches(valueOf<T>(), predicate)
}

inline fun <reified T> matches(placeholder: T, noinline predicate: (T) -> Boolean): T {
    return Matchers.enqueue(PredicateMatcher(T::class, placeholder, predicate))
}

inline fun <reified T> notMatches(noinline predicate: (T) -> Boolean): T {
    return notMatches(valueOf<T>(), predicate)
}

inline fun <reified T> notMatches(placeholder: T, noinline predicate: (T) -> Boolean): T {
    return Matchers.enqueue(NotPredicateMatcher(T::class, placeholder, predicate))
}
