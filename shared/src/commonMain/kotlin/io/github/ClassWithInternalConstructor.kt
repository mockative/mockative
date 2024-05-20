package io.github

@Mockable
internal class ClassWithInternalConstructor {
    val text: String

    internal constructor(text: String) {
        this.text = text
    }
}
