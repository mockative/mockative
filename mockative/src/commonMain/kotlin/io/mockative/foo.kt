package io.mockative

interface DemoService {
    var prop: Int

    fun prop() {

    }

    fun prop(value: Int) {

    }

    fun thing(str: String): String {
        TODO()
    }

    suspend fun asyncThing() {

    }
}

fun foo(service: DemoService) {
    whenSuspending(service, "asyncThing")
        .then { println("whenSuspending(service, \"asyncThing\")") }

    whenSuspending(service, service::asyncThing)
        .then { println("whenSuspending(service, service::asyncThing)") }

    whenInvoking(service, service::thing)
        .with(any())
        .then { println("whenInvoking[1]"); "abc" }

    whenInvoking(service, "prop")
        .with(any<Int>())
        .then { println("whenInvoking(name)") }

    whenInvoking1(service, service::prop)
        .with(any())
        .then { println("whenInvoking1") }

    whenGetting(service, service::prop)
        .then { println("whenGetting"); 1 }

    whenSetting(service, service::prop)
        .to(any())
        .then { println("whenSetting(service, service::prop).to(any())") }

    whenSetting(service, service::prop)
        .to(oneOf(5, 1, 4))
        .then { println("whenSetting(service, service::prop).to(oneOf(5, 1, 4))") }

    given(service) { thing("abc") }
        .then { "def" }

    given(service) { prop() }
        .then { println("given(service) { prop() }") }

    given(service) { prop(5) }
        .then { println("given(service) { prop(5) }") }

    given(service) { prop }
        .then { println("given(service) { prop }"); 5 }

    given(service) { prop = 2 }
        .then { println("given(service) { prop = 2 }") }

    givenSuspend(service) { asyncThing() }
        .then { println("givenSuspend(service) { asyncThing() }") }
}
