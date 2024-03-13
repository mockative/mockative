package io.mockative.kotlinpoet

import com.squareup.kotlinpoet.*
import io.mockative.*

internal fun ProcessableFunction.buildFunSpec(): FunSpec {
    val modifiers = buildModifiers()
    val returnsUnit = if (returnType == UNIT) "true" else "false"

    val invocation = if (isSuspend) "suspend" else "invoke"

    val argumentsList = buildArgumentList()
    val listOfArguments = buildListOfArgument(argumentsList)
    val parameterSpecs = buildParameterSpecs()

    val builder = FunSpec.builder(name)

    val receiver = declaration.extensionReceiver?.toTypeNameMockative(typeParameterResolver)
    if (receiver != null) {
        builder.receiver(receiver)
    }

    builder
        .addModifiers(modifiers)
        .returns(returnType.applySafeAnnotations())
        .addParameters(parameterSpecs)
        .addTypeVariables(typeVariables)

    if (isFromAny) {
        builder.addStatement("return %T.%L<%T>(this, %T(%S, %L), { super.%L(%L) })", MOCKABLE, invocation, returnType, INVOCATION_FUNCTION, name, listOfArguments, name, argumentsList)
    } else {
        val callSpyInstance = buildCallSpyInstanceBlock(receiver != null, argumentsList)
        builder.addStatement("return %T.%L<%T>(this, %T(%S, %L), %L){ %L }", MOCKABLE, invocation, returnType, INVOCATION_FUNCTION, name, listOfArguments, returnsUnit, callSpyInstance)
    }

    return builder.build()
}

private val spyKnownAnnotationsOnFunctionProblemsNames = listOf(
    "sort", // list of annotations are empty
)
private fun ProcessableFunction.buildCallSpyInstanceBlock(
    hasReceiver: Boolean,
    argumentsList: CodeBlock
): CodeBlock {
    val callSpyInstance = if (hasReceiver) "this.`${name}`" else "$spyInstanceName!!.`${name}`"
    val containsDeprecatedAnnotation = declaration.annotations.toList().any { annotation ->
        annotation.shortName.asString() == "Deprecated"
    }
    val suppressError = if (containsDeprecatedAnnotation || name in spyKnownAnnotationsOnFunctionProblemsNames) {
        AnnotationSpec.builder(SUPPRESS_ANNOTATION)
            .addMember("%S", "DEPRECATION_ERROR")
            .build()
    } else null

    val builder = CodeBlock.builder()
    if (suppressError != null) {
        builder
            .add("%L%L(%L)", suppressError, callSpyInstance, argumentsList)
    } else {
        builder
            .add("%L(%L)", callSpyInstance, argumentsList)
    }

    return builder.build()
}

private fun ProcessableFunction.buildModifiers() = buildList {
    add(KModifier.OVERRIDE)

    if (isSuspend) {
        add(KModifier.SUSPEND)
    }
}

private fun ProcessableFunction.buildArgumentList(): CodeBlock {
    val argumentsListFormat = declaration.parameters.joinToString(", ") {
        if (it.isVararg) "*`%L`" else "`%L`"
    }
    val arguments = declaration.parameters.map { it.name!!.asString() }

    return CodeBlock.builder()
        .add(argumentsListFormat, *arguments.toTypedArray())
        .build()
}

private fun ProcessableFunction.buildListOfArgument(argumentList: CodeBlock): CodeBlock {
    return CodeBlock.builder()
        .add("%M<%T?>(%L)", LIST_OF, ANY, argumentList)
        .build()
}

private fun ProcessableFunction.buildParameterSpecs() = declaration.parameters
    .map { parameter ->
        val name = parameter.name!!.asString()
        val type = parameter.type.toTypeNameMockative(typeParameterResolver)

        val checkedType = type.applySafeAnnotations()

        val modifiers = buildList {
            if (parameter.isVararg) {
                add(KModifier.VARARG)
            }
        }

        ParameterSpec.builder(name, checkedType, modifiers)
            .build()
    }
