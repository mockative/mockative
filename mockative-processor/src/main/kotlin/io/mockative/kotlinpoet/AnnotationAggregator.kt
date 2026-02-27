package io.mockative.kotlinpoet

import com.google.devtools.ksp.symbol.KSAnnotated
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import io.mockative.DEPRECATED_ANNOTATION
import io.mockative.MOCKABLE_ANNOTATION
import io.mockative.SUPPRESS_ANNOTATION
import io.mockative.kotlin.metadata.KotlinMetadata

class AnnotationAggregator {
    private val defaultAnnotations = arrayOf(
        AnnotationSpec.builder(SUPPRESS_ANNOTATION)
            .addMember("%S", "DEPRECATION")
            .addMember("%S", "DEPRECATION_ERROR")
            .addMember("%S", "OVERRIDE_DEPRECATION")
            .addMember("%S", "all") // Suppresses all Detekt warnings
            .build()
    )

    private val annotations = mutableListOf<AnnotationSpec>(*defaultAnnotations)

    private val ignoredAnnotations = setOf(MOCKABLE_ANNOTATION, DEPRECATED_ANNOTATION)

    fun addAll(declaration: KSAnnotated) {
        declaration.annotations.forEach { annotation ->
            val spec = annotation.toAnnotationSpec()
            if (isIncluded(spec)) {
                add(spec)
            }
        }
    }

    private fun isIncluded(spec: AnnotationSpec): Boolean {
        return spec.typeName !in ignoredAnnotations
    }

    private val mergeableAnnotations = setOf(SUPPRESS_ANNOTATION)

    fun add(spec: AnnotationSpec) {
        val typeNameString = "${spec.typeName}"

        val metadata = KotlinMetadata.readClassMetadata(typeNameString)
        if (metadata != null && !KotlinMetadata.isPublicType(metadata)) {
            return
        }

        if (spec.typeName in mergeableAnnotations) {
            merge(spec)
        } else {
            annotations.add(spec)
        }
    }

    private fun merge(spec: AnnotationSpec) {
        val currentIndex = annotations.indexOfFirst { it.typeName == spec.typeName }
        if (currentIndex > -1) {
            annotations[currentIndex] = annotations[currentIndex].toBuilder()
                .also { it.members.addAll(spec.members) }
                .build()
        } else {
            annotations.add(spec)
        }
    }

    fun build(): List<AnnotationSpec> {
        return annotations
    }
}
