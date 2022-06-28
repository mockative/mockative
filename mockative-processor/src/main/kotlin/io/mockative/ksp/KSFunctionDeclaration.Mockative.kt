package io.mockative.ksp

import com.google.devtools.ksp.symbol.KSClassifierReference
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

fun KSFunctionDeclaration.isFromAny() =
    when (simpleName.asString()) {
        "hashCode", "toString" -> typeParameters.isEmpty() && parameters.isEmpty()
        else -> when {
            isAnyEquals() -> true
            else -> false
        }
    }

private fun KSFunctionDeclaration.isAnyEquals(): Boolean {
    if (simpleName.asString() != "equals") return false
    if (typeParameters.isNotEmpty()) return false
    if (parameters.size != 1) return false

    val parameter = parameters[0]
    if (parameter.name?.asString() != "other") return false

    val parameterType = parameter.type.element
    if (parameterType !is KSClassifierReference) return false
    if (parameterType.referencedName() != "Any") return false

    return true
}
