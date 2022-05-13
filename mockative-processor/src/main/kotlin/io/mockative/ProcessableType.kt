package io.mockative

import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver

data class ProcessableType(
    val declaration: KSClassDeclaration,
    val sourceClassName: ClassName,
    val mockClassName: ClassName,
    val functions: List<ProcessableFunction>,
    val properties: List<ProcessableProperty>,
) {
    companion object {
        @OptIn(KotlinPoetKspPreview::class)
        private fun fromDeclaration(declaration: KSClassDeclaration): ProcessableType {
            val sourceClassName = declaration.toClassName()
            val simpleNames = sourceClassName.simpleNames.dropLast(1) + "${sourceClassName.simpleName}Mock"
            val mockClassName = ClassName(sourceClassName.packageName, *simpleNames.toTypedArray())

            val typeParameterResolver = declaration.typeParameters
                .toTypeParameterResolver()

            val functions = declaration.getAllFunctions()
                .filter { it.isPublic() }
                .map { ProcessableFunction.fromDeclaration(it, typeParameterResolver) }
                .toList()

            val properties = declaration.getAllProperties()
                .filter { it.isPublic() }
                .map { ProcessableProperty.fromDeclaration(it, typeParameterResolver) }
                .toList()

            return ProcessableType(declaration, sourceClassName, mockClassName, functions, properties)
        }

        @OptIn(KotlinPoetKspPreview::class)
        fun fromResolver(resolver: Resolver): Sequence<ProcessableType> {
            return resolver.getSymbolsWithAnnotation(MOCKABLE_TYPE_ANNOTATION.canonicalName)
                .mapNotNull { symbol -> symbol as? KSClassDeclaration }
                .filter { classDec -> classDec.classKind == ClassKind.INTERFACE }
                .map { classDec -> fromDeclaration(classDec) }
        }
    }
}