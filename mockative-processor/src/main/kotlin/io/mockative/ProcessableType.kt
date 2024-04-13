package io.mockative

import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.isConstructor
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeVariableName
import io.mockative.ksp.isFromAny
import kotlin.properties.Delegates

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
    private val children: List<ProcessableType>,
    val constructorParameters: List<KSValueParameter>,
    val spyInstanceName: String = "spyInstance",
) {
    companion object {
        private var stubsUnitByDefault by Delegates.notNull<Boolean>()

        private fun isKotlinPackage(packageName: String): Boolean {
            return packageName == "kotlin" || packageName.startsWith("kotlin.")
        }

        private fun KSClassDeclaration.getPublicConstructor(): KSFunctionDeclaration? {
            return getConstructors().firstOrNull { it.isPublic() }
        }

        private fun processConstructorParameters(
            declaration: KSClassDeclaration,
        ): Pair<List<KSValueParameter>, List<ProcessableType>> {
            val constructor = declaration.getPublicConstructor()
            if (constructor == null) {
                return emptyList<KSValueParameter>() to emptyList()
            }

            val constructorParameters = constructor.parameters
            val processableTypes = constructorParameters.mapNotNull { parameter ->
                val paramDec = parameter.type.resolve().declaration as? KSClassDeclaration
                if (paramDec == null) {
                    return@mapNotNull null
                }

                if (paramDec.classKind == ClassKind.CLASS) {
                    if (paramDec.getPublicConstructor() == null) {
                        return@mapNotNull null
                    }
                }

                val modifiers = paramDec.modifiers
                val isNotFinal = !modifiers.contains(Modifier.FINAL) && !modifiers.contains(Modifier.SEALED)

                val usages = listOfNotNull(paramDec.containingFile)

                return@mapNotNull if (isNotFinal) {
                    fromDeclaration(
                        declaration = paramDec,
                        usages = usages,
                    )
                } else null
            }

            return constructorParameters to processableTypes
        }

        private fun fromDeclaration(
            declaration: KSClassDeclaration,
            usages: List<KSFile>,
        ): ProcessableType {
            val sourceClassName = declaration.toClassName()
            val sourcePackageName = sourceClassName.packageName

            // Not allowed to use the kotlin package as base when creating new classes
            val mockPackageNamePrefix = if (isKotlinPackage(sourcePackageName)) "io.mockative." else ""

            val simpleNamePrefix = sourceClassName.simpleNames.dropLast(1)
            val simpleNameSuffix = "${sourceClassName.simpleName}Mock"

            val mockSimpleNames = simpleNamePrefix + simpleNameSuffix
            val mockSimpleName = mockSimpleNames.joinToString("_")
            val mockClassName = ClassName(mockPackageNamePrefix + sourcePackageName, mockSimpleName)

            val typeParameterResolver = declaration.typeParameters
                .toTypeParameterResolver()

            val functions = declaration.getAllFunctions()
                // Functions from Any are manually implemented in [Mockable]
                .filter { it.isPublic() && !it.isConstructor() && !it.modifiers.contains(Modifier.FINAL) }
                .map { ProcessableFunction.fromDeclaration(it, typeParameterResolver) }
                .toList()

            val properties = declaration.getAllProperties()
                .filter { it.isPublic() }
                .map { ProcessableProperty.fromDeclaration(it, typeParameterResolver) }
                .toList()

            val typeVariables = declaration.typeParameters
                .map { it.toTypeVariableName(typeParameterResolver) }

            val (constructorParameters, children) = processConstructorParameters(declaration)
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
                children = children,
                constructorParameters = constructorParameters,
            )

            functions.forEach { it.parent = processableType }

            return processableType
        }

        fun fromResolver(resolver: Resolver, stubsUnitByDefault: Boolean): List<ProcessableType> {
            this.stubsUnitByDefault = stubsUnitByDefault

            val processableTypes = resolver.getSymbolsWithAnnotation(MOCK_ANNOTATION.canonicalName)
                .filterIsInstance<KSPropertyDeclaration>()
                .mapNotNull { property ->
                    (property.type.resolve().declaration as? KSClassDeclaration)
                        ?.let { classDec -> property.containingFile?.let { classDec to it }  }
                }
                .filter { (classDec, _) -> classDec.classKind == ClassKind.INTERFACE || classDec.classKind == ClassKind.CLASS }
                .groupBy({ (classDec, _) -> classDec }, { (_, usage) -> usage })
                .map { (classDec, usages) -> fromDeclaration(classDec, usages) }
                .flatten()
                .distinctBy { it.mockClassName }

            return processableTypes
        }

        private fun List<ProcessableType>.flatten(): List<ProcessableType> {
            return this + flatMap { type -> type.children }
        }
    }
}
