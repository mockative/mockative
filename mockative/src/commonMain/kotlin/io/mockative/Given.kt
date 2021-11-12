package io.mockative

fun <T : Any> given(receiver: T): GivenBuilder<T> = GivenBuilder(receiver)

inline fun <T : Any> givenMock(receiver: T): GivenBuilder<T> = given(receiver)
inline fun <T : Any> setup(receiver: T): GivenBuilder<T> = given(receiver)

suspend fun demo2(service: DemoService) {
    given(service).invocation { service.transformData("abc") }
        .thenReturn("abc")

    given(service).coroutine { service.refreshData() }
        .thenDoNothing()

    given(service).invocation { service.mutableProperty }
        .thenReturn("abc")

    given(service).invocation { service.mutableProperty = "abc" }
        .thenDoNothing()

    givenMock(service).function(service::transformData)
        .whenInvokedWith(any())
        .thenReturn("abc")

    setup(service).suspendFunction(service::refreshData)
        .whenInvoked()
        .thenDoNothing()

    given(service).suspendFunction(service::fetchData)
        .whenInvokedWith(any(), any())
        .thenDoNothing()

    var mutableProperty = "defaultValue"

    given(service).getter(service::mutableProperty)
        .whenInvoked()
        .then { mutableProperty }

    given(service).setter(service::mutableProperty)
        .whenInvokedWith(any())
        .then { mutableProperty = it }

    given(service).suspendFunction(service::overload, fun2<Int, String>())
}
