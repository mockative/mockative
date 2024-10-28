package io.mockative

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
abstract class MockativeExtension @Inject constructor(objects: ObjectFactory) {
    internal val optIn: MapProperty<String, List<String>> = objects.mapProperty(String::class.java, List::class.java) as MapProperty<String, List<String>>

    private fun appendOptIn(pckg: String, annotation: String) {
        optIn.put(pckg, optIn.get().getOrDefault(pckg, emptyList()) + annotation)
    }

    fun optIn(annotation: String) = appendOptIn("*", annotation)

    fun optIn(pckg: String, annotation: String) = appendOptIn(pckg, annotation)

    internal val excludeMembers: SetProperty<String> = objects.setProperty(String::class.java)

    fun excludeMember(member: String) {
        excludeMembers.add(member)
    }

    val excludeKotlinDefaultMembers: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

    val stubsUnitByDefault: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

    fun forPackage(packageName: String, block: PackageExtension.() -> Unit) {
        PackageExtension(packageName, this).block()
    }
}
