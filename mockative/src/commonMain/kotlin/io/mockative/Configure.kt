package io.mockative

fun <T : Any> configure(subject: T, block: MockConfiguration.() -> Unit): T {
    return subject.apply { block(MockConfiguration(Mockable.mockable(subject))) }
}

class MockConfiguration(private val mock: Mockable) {
    var stubsUnitByDefault: Boolean
        get() = mock.stubsUnitsByDefault
        set(value) { mock.stubsUnitsByDefault = value }
}
