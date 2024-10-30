package io.mockative

import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.getKotlinClassByName
import com.google.devtools.ksp.isConstructor
import com.google.devtools.ksp.isInternal
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeVariableName
import io.mockative.kotlinpoet.fullSimpleName
import kotlin.reflect.KClass

data class ProcessableType(
    val configuration: MockativeConfiguration,
    val declaration: KSClassDeclaration,
    val sourceClassName: ClassName,
    val mockClassName: ClassName,
    val functions: List<ProcessableFunction>,
    val properties: List<ProcessableProperty>,
    val usages: List<KSFile>,
    val typeParameterResolver: TypeParameterResolver,
    val typeVariables: List<TypeVariableName>,
    private val children: List<ProcessableType>,
    val constructorParameters: List<KSValueParameter>,
) {
    companion object {
        private fun isKotlinPackage(packageName: String): Boolean {
            return packageName == "kotlin" || packageName.startsWith("kotlin.")
        }

        private fun KSClassDeclaration.getPublicConstructor(): KSFunctionDeclaration? {
            val constructors = getConstructors()
            return constructors.firstOrNull { it.isPublic() } ?: constructors.firstOrNull { it.isInternal() }
        }

        private fun processConstructorParameters(
            configuration: MockativeConfiguration,
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
                val isFinal = modifiers.contains(Modifier.FINAL) || modifiers.contains(Modifier.SEALED)
                if (isFinal) {
                    return@mapNotNull null
                }

                val usages = listOfNotNull(paramDec.containingFile)

                return@mapNotNull fromDeclaration(
                    configuration,
                    declaration = paramDec,
                    usages = usages,
                )
            }

            return constructorParameters to processableTypes
        }

        private fun fromDeclaration(
            configuration: MockativeConfiguration,
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
                .filter {
                    try {
                        it.isPublic() && !it.isConstructor() && !it.modifiers.contains(Modifier.FINAL)
                    } catch (e: IllegalStateException) {
                        false
                    }
                }
                .map { ProcessableFunction.fromDeclaration(it, typeParameterResolver) }
                .toList()

            val properties = declaration.getAllProperties()
                .filter {
                    try {
                        it.isPublic()
                    } catch (e: IllegalStateException) {
                        false
                    }
                }
                .map { ProcessableProperty.fromDeclaration(it, typeParameterResolver) }
                .toList()

            val typeVariables = declaration.typeParameters
                .map { it.toTypeVariableName(typeParameterResolver) }

            val (constructorParameters, children) = processConstructorParameters(configuration, declaration)
            val processableType = ProcessableType(
                configuration = configuration,
                declaration = declaration,
                sourceClassName = sourceClassName,
                mockClassName = mockClassName,
                functions = functions,
                properties = properties,
                usages = usages,
                typeParameterResolver = typeParameterResolver,
                typeVariables = typeVariables,
                children = children,
                constructorParameters = constructorParameters,
            )

            functions.forEach { it.parent = processableType }

            return processableType
        }

        fun fromResolver(configuration: MockativeConfiguration, resolver: Resolver): List<ProcessableType> {
            val processableTypes = resolver.getSymbolsWithAnnotation(MOCKABLE_ANNOTATION.canonicalName)
                .filterIsInstance<KSClassDeclaration>()
                .flatMap { classDec ->
                    classDec.getAnnotationsByClassName(MOCKABLE_ANNOTATION)
                        .flatMap { mockable ->
                            val types = mockable.getValue("types", emptyList<KSType>())
                            if (types.isEmpty()) {
                                listOf(classDec to classDec.containingFile)
                            } else {
                                types
                                    .mapNotNull { it.declaration as? KSClassDeclaration }
                                    .map { declaredClassDec -> declaredClassDec to classDec.containingFile }
                            }
                        }
                }
                .groupBy({ (classDec, _) -> classDec }, { (_, usage) -> usage })
                .map { (classDec, usages) -> fromDeclaration(configuration, classDec, usages.filterNotNull()) }
                .flatten()
                .distinctBy { it.mockClassName }

            return processableTypes
        }

        private fun List<ProcessableType>.flatten(): List<ProcessableType> {
            return this + flatMap { type -> type.children.flatten() }
        }
    }
}

fun KSAnnotated.getAnnotationsByClassName(name: ClassName): Sequence<KSAnnotation> {
    return annotations.filter { it.annotationType.toTypeName() == name }
}

@Suppress("UNCHECKED_CAST")
fun <T> KSAnnotation.getValue(name: String, defaultValue: T): T {
    val argument = arguments.firstOrNull { it.name!!.asString() == name } ?: return defaultValue
    return argument.value as T
}
