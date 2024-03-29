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
    private var children: List<ProcessableType>,
    val constructorParameters: List<KSValueParameter>,
    val generateTypeFunctions: List<ShouldGenerateTypeFunction>,
    val spyInstanceName: String = "spyInstance",
) {
    private enum class ClassDeclMockType(val generateTypeFunction: ShouldGenerateTypeFunction) {
        MOCK(ShouldGenerateTypeFunction.MOCK),
        SPY(ShouldGenerateTypeFunction.SPY);
    }

    enum class ShouldGenerateTypeFunction {
        MOCK,
        SPY,
        NONE;
    }

    private data class SymbolProcessingInformation(
        val classDeclaration: KSClassDeclaration,
        val usage: KSFile?,
        val mockType: ClassDeclMockType,
    )

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
                ?: return emptyList<KSValueParameter>() to emptyList<ProcessableType>()


            val constructorParameters = constructor.parameters
            val processableTypes = constructorParameters.mapNotNull { parameter ->
                val parameterDeclaration =
                    parameter.type.resolve().declaration as? KSClassDeclaration ?: return@mapNotNull null
                if (parameterDeclaration.classKind == ClassKind.CLASS) parameterDeclaration.getPublicConstructor()
                    ?: return@mapNotNull null

                val modifiers = parameterDeclaration.modifiers
                val isNotFinal = !modifiers.contains(Modifier.FINAL) && !modifiers.contains(Modifier.SEALED)

                val containingFilesOfClass = parameterDeclaration.containingFile?.let { listOf(it) } ?: emptyList()

                return@mapNotNull if (isNotFinal) {
                    fromDeclaration(
                        declaration = parameterDeclaration,
                        usages = containingFilesOfClass,
                        generateTypeFunctions = listOf(ShouldGenerateTypeFunction.NONE),
                    )
                } else null
            }

            return constructorParameters to processableTypes
        }

        private fun fromDeclaration(
            declaration: KSClassDeclaration,
            usages: List<KSFile>,
            generateTypeFunctions: List<ShouldGenerateTypeFunction>,
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
                .filter { it.isPublic() && !it.isConstructor() && !it.isFromAny() && !it.modifiers.contains(Modifier.FINAL) }
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
                generateTypeFunctions = generateTypeFunctions,
            )

            functions.forEach { it.parent = processableType }

            return processableType
        }

        fun fromResolver(resolver: Resolver, stubsUnitByDefault: Boolean): List<ProcessableType> {
            this.stubsUnitByDefault = stubsUnitByDefault

            val processableTypes = resolver.getSymbolsWithAnnotation(MOCK_ANNOTATION.canonicalName)
                .filterIsInstance<KSPropertyDeclaration>()
                .mapNotNull { property ->
                    val mockAnnotation =
                        property.annotations.find { it.shortName.asString() == MOCK_ANNOTATION.simpleName }
                    val isSpy =
                        mockAnnotation?.arguments?.find { it.name?.asString() == "isSpy" }?.value as? Boolean ?: false
                    val mockType = if (isSpy) ClassDeclMockType.SPY else ClassDeclMockType.MOCK

                    (property.type.resolve().declaration as? KSClassDeclaration)
                        ?.let { SymbolProcessingInformation(it, property.containingFile, mockType) }
                }
                .filter { (classDec, _, _) -> classDec.classKind == ClassKind.INTERFACE || classDec.classKind == ClassKind.CLASS }
                .groupBy({ it.classDeclaration }) { it.usage to it.mockType }
                .mapValues { (_, fileAndMockTypesInformation) ->
                    val (usages, mockTypes) = fileAndMockTypesInformation.unzip()
                    val combinedMockTypes = mockTypes.map { it.generateTypeFunction }.distinct()
                    val combinedUsages = usages.filterNotNull().distinct()

                    return@mapValues combinedUsages to combinedMockTypes
                }.map { (classDec, fileAndMockTypesInformation) ->
                    val (usages, mockTypes) = fileAndMockTypesInformation

                    fromDeclaration(
                        declaration = classDec,
                        usages = usages,
                        generateTypeFunctions = mockTypes,
                    )
                }
                .flatten()
                .distinctBy { it.mockClassName }
                .removeChildren()

            return processableTypes
        }

        private fun List<ProcessableType>.removeChildren(): List<ProcessableType> {
            return this.onEach { it.children = emptyList() }
        }


        private fun List<ProcessableType>.flatten(): List<ProcessableType> {
            return this + this.flatMap { type ->
                type.children.flatten()
            }
        }
    }
}