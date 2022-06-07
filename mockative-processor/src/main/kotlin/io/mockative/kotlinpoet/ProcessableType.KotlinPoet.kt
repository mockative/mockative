package io.mockative.kotlinpoet

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.mockative.KCLASS
import io.mockative.MOCKABLE
import io.mockative.ProcessableType
import io.mockative.SUPPRESS_ANNOTATION
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

    return FunSpec.builder("mock")
        .addModifiers(modifiers)
        .addTypeVariables(typeVariables)
        .addParameter(type)
        .returns(parameterizedSourceClassName)
        .addStatement("return %T()", parameterizedMockClassName)
        .addOriginatingKSFiles(usages)
        .build()
}

internal fun ProcessableType.buildMockTypeSpec(): TypeSpec {
    val properties = buildPropertySpecs()
    val functions = buildFunSpecs()

    val parameterSpec = ParameterSpec.builder("stubsUnitByDefault", BOOLEAN)
        .build()

    val modifiers = buildList {
        if (declaration.isEffectivelyInternal()) {
            add(KModifier.INTERNAL)
        }
    }

    return TypeSpec.classBuilder(mockClassName)
        .addModifiers(modifiers)
        .addTypeVariables(typeVariables)
        .superclass(MOCKABLE)
        .addSuperclassConstructorParameter("%N = %L", parameterSpec, stubsUnitByDefault)
        .addSuperinterface(sourceClassName.parameterizedByAny(typeVariables))
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
