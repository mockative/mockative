package io.mockative

import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.*

@OptIn(KotlinPoetKspPreview::class)
data class ProcessableType(
    val declaration: KSClassDeclaration,
    val sourceClassName: ClassName,
    val mockClassName: ClassName,
    val functions: List<ProcessableFunction>,
    val properties: List<ProcessableProperty>,
    val usages: List<KSFile>,
    val typeParameterResolver: TypeParameterResolver,
    val typeVariables: List<TypeVariableName>,
    val stubsUnitByDefault: Boolean,
) {
    companion object {
        @OptIn(KotlinPoetKspPreview::class)
        private fun fromDeclaration(declaration: KSClassDeclaration, usages: List<KSFile>, stubsUnitByDefault: Boolean): ProcessableType {
            val sourceClassName = declaration.toClassName()
            val simpleNames = sourceClassName.simpleNames.dropLast(1) + "${sourceClassName.simpleName}Mock"
            val simpleName = simpleNames.joinToString("_")
            val mockClassName = ClassName(sourceClassName.packageName, simpleName)

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

            val typeVariables = declaration.typeParameters
                .map { it.toTypeVariableName(typeParameterResolver) }

            val processableType = ProcessableType(
                declaration = declaration,
                sourceClassName = sourceClassName,
                mockClassName = mockClassName,
                functions = functions,
                properties = properties,
                usages = usages,
                typeParameterResolver = typeParameterResolver,
                typeVariables = typeVariables,
                stubsUnitByDefault = stubsUnitByDefault,
            )

            functions.forEach { it.parent = processableType }

            return processableType
        }

        @OptIn(KotlinPoetKspPreview::class)
        fun fromResolver(resolver: Resolver, stubsUnitByDefault: Boolean): List<ProcessableType> {
            return resolver.getSymbolsWithAnnotation(MOCK_ANNOTATION.canonicalName)
                .mapNotNull { symbol -> symbol as? KSPropertyDeclaration }
                .mapNotNull { property ->
                    (property.type.resolve().declaration as? KSClassDeclaration)
                        ?.let { it to property.containingFile }
                }
                .filter { (classDec, _) -> classDec.classKind == ClassKind.INTERFACE }
                .groupBy({ (classDec, _) -> classDec }, { (_, file) -> file })
                .map { (classDec, usages) ->
                    fromDeclaration(
                        declaration = classDec,
                        usages = usages.filterNotNull(),
                        stubsUnitByDefault = stubsUnitByDefault,
                    )
                }
        }
    }
}