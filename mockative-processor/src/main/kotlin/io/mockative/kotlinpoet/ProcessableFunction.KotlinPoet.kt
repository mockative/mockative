package io.mockative.kotlinpoet

import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.UNIT
import io.mockative.INVOCATION_FUNCTION
import io.mockative.LIST_OF
import io.mockative.MOCKABLE
import io.mockative.ProcessableFunction

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
        .returns(returnType)
        .addParameters(parameterSpecs)
        .addTypeVariables(typeVariables)

    if (isFromAny) {
        builder.addStatement("return %T.%L<%T>(this, %T(%S, %L), { super.%L(%L) })", MOCKABLE, invocation, returnType, INVOCATION_FUNCTION, name, listOfArguments, name, argumentsList)
    } else {
        builder.addStatement("return %T.%L<%T>(this, %T(%S, %L), %L)", MOCKABLE, invocation, returnType, INVOCATION_FUNCTION, name, listOfArguments, returnsUnit)
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
    val argumentsListFormat = declaration.parameters.joinToString(", ") { "`%L`" }
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

        val modifiers = buildList {
            if (parameter.isVararg) {
                add(KModifier.VARARG)
            }
        }

        ParameterSpec.builder(name, type, modifiers)
            .build()
    }
