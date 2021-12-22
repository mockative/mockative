package io.mockative

import java.io.Writer

class FunctionWriter(private val writer: Writer) {
    fun appendFunction(function: MockDescriptor.Function) {
        appendIndentation()
        appendModifiers(function)
        appendStandardSyntax(function)
        appendParameters(function)
        appendReturnType(function)
        appendGenericConstraints(function)
        appendBody(function)

        writer.appendLine()
    }

    private fun appendIndentation() {
        writer.append("    ")
    }

    private fun appendModifiers(function: MockDescriptor.Function) {
        writer.append("override")

        if (function.isSuspending) {
            writer.append(" suspend")
        }
    }

    private fun appendStandardSyntax(function: MockDescriptor.Function) {
        writer.append(" fun ")

        val typeParameters = function.typeParameters
        if (typeParameters.isNotEmpty()) {
            writer.append('<')
            writer.append(typeParameters.joinToString(", ") { it.name })
            writer.append('>')
            writer.append(' ')
        }

        writer.append(function.name)
    }

    private fun appendParameters(function: MockDescriptor.Function) {
        val parameters = function.parameters
            .joinToString(", ") { parameter -> toParameterSignature(parameter) }

        writer.append('(')
        writer.append(parameters)
        writer.append(')')
    }

    private fun toParameterSignature(parameter: MockDescriptor.Function.Parameter): String {
        val modifiers = parameter.modifier?.let { "$it " } ?: ""
        return "$modifiers${parameter.name}: ${parameter.type}"
    }

    private fun appendReturnType(function: MockDescriptor.Function) {
        writer.append(": ")
        writer.append(function.returnType)
    }

    private fun appendGenericConstraints(function: MockDescriptor.Function) {
        val genericConstraints = function.typeParameters
            .flatMap { typeParameter -> typeParameter.bounds.map { bound -> typeParameter to bound } }
            .joinToString(", ") { (typeParameter, bound) -> "${typeParameter.name} : $bound" }

        if (genericConstraints.isNotEmpty()) {
            writer.append(" where ")
            writer.append(genericConstraints)
        }
    }

    private fun appendBody(function: MockDescriptor.Function) {
        writer.append(" = ")

        writer.append("io.mockative.Mockable.")
        if (function.isSuspending) {
            writer.append("suspend")
        } else {
            writer.append("invoke")
        }

        writer.append('<')
        writer.append(function.returnType)
        writer.append('>')

        writer.append('(')
        writer.append("this, ")
        writer.append("io.mockative.Invocation.Function(")
        writer.append('"')
        writer.append(function.name)
        writer.append('"')

        if (function.parameters.isNotEmpty()) {
            writer.append(", listOf<Any?>(")

            val arguments = function.parameters
                .joinToString(", ") { parameter -> parameter.name }

            writer.append(arguments)
            writer.append(")")
        } else {
            writer.append(", emptyList<Any?>()")
        }

        writer.append(')')
        writer.append(", ")
        writer.append(if (function.returnType == "kotlin.Unit") "true" else "false")
        writer.append(')')
    }
}