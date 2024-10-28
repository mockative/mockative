package io.mockative

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
abstract class MockativeExtension @Inject constructor(objects: ObjectFactory) {
    internal val optIn: MapProperty<String, List<String>> = objects.mapProperty(String::class.java, List::class.java) as MapProperty<String, List<String>>

    fun optIn(annotation: String) {
        optIn.put("*", optIn.get().getOrDefault("*", emptyList()) + annotation)
    }

    internal val excludeMembers: SetProperty<String> = objects.setProperty(String::class.java)

    val excludeKotlinDefaultMembers: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

    val stubsUnitByDefault: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

    fun forPackage(packageName: String, block: PackageExtension.() -> Unit) {
        PackageExtension(packageName, this).block()
    }
}
