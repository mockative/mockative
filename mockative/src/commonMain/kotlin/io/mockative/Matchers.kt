package io.mockative

import io.mockative.fake.valueOf
import io.mockative.matchers.AnyMatcher
import io.mockative.matchers.ComparableMatcher
import io.mockative.matchers.EqualsMatcher
import io.mockative.matchers.InstanceOfMatcher
import io.mockative.matchers.Matcher
import io.mockative.matchers.NotEqualsMatcher
import io.mockative.matchers.NotInstanceOfMatcher
import io.mockative.matchers.NotOneOfMatcher
import io.mockative.matchers.NotPredicateMatcher
import io.mockative.matchers.OneOfMatcher
import io.mockative.matchers.PredicateMatcher

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

/**
 * Matches any value that's equal to the given [value].
 *
 * @param value The value to match.
 * @return The [value]
 */
inline fun <reified T> eq(value: T): T {
    return Matchers.enqueue(EqualsMatcher(value))
}

/**
 * Matches any value that's not equal to the given [value].
 *
 * @param value The value to match.
 * @return The [value]
 */
inline fun <reified T> ne(value: T): T {
    return Matchers.enqueue(NotEqualsMatcher(value))
}

/**
 * Matches one of the provided [values].
 *
 * @param values The values to match.
 * @return The first value in [values].
 * @see in
 */
inline fun <reified T> oneOf(vararg values: T): T {
    return Matchers.enqueue(OneOfMatcher(values.toList()))
}

/**
 * Matches one of the provided [values].
 *
 * @param values The values to match.
 * @return The first value in [values].
 * @see oneOf
 */
inline fun <reified T> `in`(values: Iterable<T>): T {
    return Matchers.enqueue(OneOfMatcher(values.toList()))
}

/**
 * Matches none of the provided [values].
 *
 * @param values The values to ignore.
 * @return The first value in [values].
 * @see notIn
 */
inline fun <reified T> notOneOf(vararg values: T): T {
    return Matchers.enqueue(NotOneOfMatcher(values.toList()))
}

/**
 * Matches none of the provided [values].
 *
 * @param values The values to ignore.
 * @return The first value in [values].
 * @see notOneOf
 */
inline fun <reified T> notIn(values: Iterable<T>): T {
    return Matchers.enqueue(NotOneOfMatcher(values.toList()))
}

/**
 * Matches a value greater than the specified [value].
 *
 * @param value The value to match values greater than.
 * @return The [value]
 */
inline fun <reified T : Comparable<T>> gt(value: T): T {
    return Matchers.enqueue(ComparableMatcher(T::class, value, ">") { a, b -> a > b })
}

/**
 * Matches a value greater than or equal to the specified [value].
 *
 * @param value The value to match values greater than or equal to.
 * @return The [value]
 */
inline fun <reified T : Comparable<T>> gte(value: T): T {
    return Matchers.enqueue(ComparableMatcher(T::class, value, ">=") { a, b -> a >= b })
}

/**
 * Matches a value less than the specified [value].
 *
 * @param value The value to match values less than.
 * @return The [value]
 */
inline fun <reified T : Comparable<T>> lt(value: T): T {
    return Matchers.enqueue(ComparableMatcher(T::class, value, "<") { a, b -> a < b })
}

/**
 * Matches a value less than or equal to the specified [value].
 *
 * @param value The value to match values less than.
 * @return The [value]
 */
inline fun <reified T : Comparable<T>> lte(value: T): T {
    return Matchers.enqueue(ComparableMatcher(T::class, value, "<=") { a, b -> a <= b })
}

/**
 * Matches a value that matches the specified [predicate].
 *
 * @param predicate The predicate to match.
 * @return A generated value of the specified type [T]
 */
inline fun <reified T> matches(noinline predicate: (T) -> Boolean): T {
    return matches(valueOf<T>(), predicate)
}

/**
 * Matches a value that matches the specified [predicate] while specifying a valid value
 * (placeholder) for use during stubbing.
 *
 * @param placeholder A value used as a placeholder during stubbing.
 * @param predicate The predicate to match.
 * @return The [placeholder]
 */
inline fun <reified T> matches(placeholder: T, noinline predicate: (T) -> Boolean): T {
    return Matchers.enqueue(PredicateMatcher(T::class, placeholder, predicate))
}

/**
 * Matches a value that does not match the specified [predicate].
 *
 * @param predicate The predicate to match values that don't match.
 * @return A generated value of the specified type [T]
 */
inline fun <reified T> notMatches(noinline predicate: (T) -> Boolean): T {
    return notMatches(valueOf<T>(), predicate)
}

/**
 * Matches a value that does not match the specified [predicate] while specifying a valid value
 * (placeholder) for use during stubbing.
 *
 * @param placeholder A value used as a placeholder during stubbing.
 * @param predicate The predicate to match values that don't match.
 * @return The [placeholder]
 */
inline fun <reified T> notMatches(placeholder: T, noinline predicate: (T) -> Boolean): T {
    return Matchers.enqueue(NotPredicateMatcher(T::class, placeholder, predicate))
}
