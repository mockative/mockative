package io.mockative.kotlinpoet

import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.UNIT
import io.mockative.INVOCATION_FUNCTION
import io.mockative.LIST_OF
import io.mockative.MOCKED
import io.mockative.ProcessableFunction

internal fun ProcessableFunction.buildFunSpec(): FunSpec {
    val modifiers = buildModifiers()
    val returnsUnit = if (returnType == UNIT) "true" else "false"

    val invocation = if (isSuspend) "suspend" else "invoke"

    val argumentsList = buildArgumentList()
    val parameterSpecs = buildParameterSpecs()

    return FunSpec.builder(name)
        .let { builder ->
            declaration.extensionReceiver?.toTypeNameMockative(typeParameterResolver)
                ?.let { receiver -> builder.receiver(receiver) } ?: builder
        }
        .addModifiers(modifiers)
        .returns(returnType)
        .addParameters(parameterSpecs)
        .addTypeVariables(typeVariables)
        .addStatement("return %T.%L<%T>(this, %T(%S, %L), %L)", MOCKED, invocation, returnType, INVOCATION_FUNCTION, name, argumentsList, returnsUnit)
        .build()
}

private fun ProcessableFunction.buildModifiers() = buildList {
    add(KModifier.OVERRIDE)

    if (isSuspend) {
        add(KModifier.SUSPEND)
    }
}

private fun ProcessableFunction.buildArgumentList(): CodeBlock {
    val argumentsListFormat = declaration.parameters.joinToString(", ") { "`%L`" }
    val arguments = declaration.parameters.map { it.name!!.asString() }

    val argumentsListValues = CodeBlock.builder()
        .add(argumentsListFormat, *arguments.toTypedArray())
        .build()

    return CodeBlock.builder()
        .add("%M<%T?>(%L)", LIST_OF, ANY, argumentsListValues)
        .build()
}

private fun ProcessableFunction.buildParameterSpecs() = declaration.parameters
    .map { parameter ->
        val name = parameter.name!!.asString()
        val type = parameter.type.toTypeNameMockative(typeParameterResolver)

        val modifiers = buildList {
            if (parameter.isVararg) {
                add(KModifier.VARARG)
            }
        }

        ParameterSpec.builder(name, type, modifiers)
            .build()
    }
