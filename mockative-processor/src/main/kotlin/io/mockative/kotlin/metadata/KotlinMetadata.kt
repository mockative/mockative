package io.mockative.kotlin.metadata

import io.mockative.log
import kotlin.metadata.Visibility
import kotlin.metadata.jvm.KotlinClassMetadata
import kotlin.metadata.visibility

object KotlinMetadata {
    fun isPublicType(metadata: KotlinClassMetadata): Boolean {
        return metadata is KotlinClassMetadata.Class && metadata.kmClass.visibility == Visibility.PUBLIC
    }

    fun readClassMetadata(typeName: String): KotlinClassMetadata? {
        return findMetadataAnnotation(typeName)?.let { KotlinClassMetadata.Companion.readLenient(it) }
    }

    private fun findMetadataAnnotation(typeName: String): Metadata? {
        val currentThread = Thread.currentThread()
        val classLoader = currentThread.contextClassLoader

        val clazz = try {
            classLoader.loadClass(typeName)
        } catch (e: ClassNotFoundException) {
            log.warn("Annotation class not found: $e")
            return null
        }

        val proxy = clazz.annotations.firstOrNull { it.annotationClass.qualifiedName == "kotlin.Metadata" }
        if (proxy == null) {
            return null
        }

        return Metadata(
            kind = proxy("k"),
            metadataVersion = proxy("mv"),
            bytecodeVersion = proxy("bv"),
            data1 = proxy("d1"),
            data2 = proxy("d2"),
            extraString = proxy("xs"),
            packageName = proxy("pn"),
            extraInt = proxy("xi")
        )
    }

    private inline operator fun <reified R> Any.invoke(name: String): R {
        return javaClass.getMethod(name).invoke(this) as R
    }
}
