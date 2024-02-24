package io.mockative.kotlinpoet

import com.google.devtools.ksp.symbol.ClassKind
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.mockative.*
import io.mockative.ksp.addOriginatingKSFiles

internal fun ProcessableType.buildMockFunSpec(): FunSpec {
    val suppressUnusedParameter = AnnotationSpec.builder(SUPPRESS_ANNOTATION)
        .addMember("%S", "UNUSED_PARAMETER")
        .build()

    val parameterizedSourceClassName = sourceClassName.parameterizedByAny(typeVariables)
    val parameterizedMockClassName = mockClassName.parameterizedByAny(typeVariables)

    val typeType = KCLASS.parameterizedBy(parameterizedSourceClassName)

    val type = ParameterSpec.builder("type", typeType)
        .addAnnotation(suppressUnusedParameter)
        .build()

    val modifiers = buildList {
        if (declaration.isEffectivelyInternal()) {
            add(KModifier.INTERNAL)
        }
    }

    val functionTypeVariables = typeVariables.map { it.withoutVariance() }

    return FunSpec.builder("mock")
        .addModifiers(modifiers)
        .addTypeVariables(functionTypeVariables)
        .addParameter(type)
        .returns(parameterizedSourceClassName)
        .addStatement("return %M(%T()) { stubsUnitByDefault = %L }", CONFIGURE, parameterizedMockClassName, stubsUnitByDefault)
        .addOriginatingKSFiles(usages)
        .build()
}

private fun TypeVariableName.withoutVariance(): TypeVariableName {
    return TypeVariableName(name = name, bounds = bounds)
}

internal fun ProcessableType.buildMockTypeSpec(generatedMockTypes: Map<String, String>): TypeSpec {
    val properties = buildPropertySpecs()
    val functions = buildFunSpecs()

    val modifiers = buildList {
        if (declaration.isEffectivelyInternal()) {
            add(KModifier.INTERNAL)
        }
    }

    val typeSpec = TypeSpec.classBuilder(mockClassName)
        .addModifiers(modifiers)
        .addTypeVariables(typeVariables)
        .buildTypeSpec(this, generatedMockTypes)

    return typeSpec
        .addProperties(properties)
        .addFunctions(functions)
        .addKdoc(declaration.docString?.trim() ?: "")
        .addOriginatingKSFiles(usages)
        .build()
}

private fun ProcessableType.buildPropertySpecs(): List<PropertySpec> {
    return properties
        .map { it.buildPropertySpec() }
        .toList()
}

private fun ProcessableType.buildFunSpecs(): List<FunSpec> {
    return functions
        .map { it.buildFunSpec() }
        .toList()
}

private fun TypeSpec.Builder.buildTypeSpec(
    processableType: ProcessableType,
    generatedMockTypes: Map<String, String>
): TypeSpec.Builder {
    val typeSpec = this
    processableType.run {
        if (declaration.classKind == ClassKind.CLASS) {
            typeSpec.superclass(sourceClassName.parameterizedByAny(typeVariables))

            constructorParameters.forEach { param ->
                val property =
                    properties.find { it.name == param.name?.asString() }?.type
                        ?: param.type.toTypeNameMockative() // if the constructor parameter is not a property, then it is private

                val constructorParameterInitialization = property.getConstructorParameterValue(generatedMockTypes)

                addSuperclassConstructorParameter("$param = %L", constructorParameterInitialization)
            }
        } else { // ClassKind.INTERFACE
            typeSpec.addSuperinterface(sourceClassName.parameterizedByAny(typeVariables))
        }
    }

    return typeSpec
}