package io.mockative

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@RequiresOptIn(message = "This API is experimental. It may be changed in the future without notice.", level = RequiresOptIn.Level.WARNING)
annotation class ConfigurationApi

@ConfigurationApi
fun <T : Any> configure(subject: T, block: MockConfiguration.() -> Unit): T {
    return subject.apply { block(MockConfiguration(asMockable())) }
}

@ConfigurationApi
class MockConfiguration(private val mock: Mockable) {
    var stubsUnitByDefault: Boolean
        get() = mock.stubsUnitsByDefault
        set(value) { mock.stubsUnitsByDefault = value }
}
