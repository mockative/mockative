package io.mockative

import io.mockative.matchers.Matcher
import io.mockative.matchers.SpecificArgumentsMatcher
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty

// TOOD Generate VerifyThatBuilder

fun <T : Any> verifyThat(mock: T): VerifyThatBuilder<T> {
    return VerifyThatBuilder(mock)
}

interface DemoService {
    var mutableProperty: String

    fun transformData(data: String): String

    suspend fun refreshData()
    suspend fun fetchData(id: String, name: String)

    suspend fun overload()
    suspend fun overload(id: String, name: String)
    suspend fun overload(id: Int, name: String)
}

suspend fun demo(service: DemoService) {
    verifyThat(service).invocation { transformData("abc") }
        .wasInvoked()

    verifyThat(service).coroutine { refreshData() }
        .wasNotInvoked()

    verifyThat(service).function("transformData")
        .with(any<String>(), any<Int>())
        .wasNotInvoked()

    verifyThat(service).function(service::transformData)
        .with(any())
        .wasNotInvoked()

    verifyThat(service).suspendFunction(service::refreshData)
        .wasInvoked(exactly = 8.times)

    verifyThat(service).suspendFunction(service::fetchData)
        .with(any(), oneOf("a", "b", "c"))
        .wasInvoked(atMost = 9)

    verifyThat(service).getter(service::mutableProperty)
        .wasInvoked(atLeast = 5)

    verifyThat(service).setter(service::mutableProperty)
        .with(eq("abc"))
        .wasInvoked(exactly = 1)
}