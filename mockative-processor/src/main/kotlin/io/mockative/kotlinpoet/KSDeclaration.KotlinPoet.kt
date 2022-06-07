package io.mockative.kotlinpoet

import com.google.devtools.ksp.isInternal
import com.google.devtools.ksp.symbol.KSDeclaration

internal fun KSDeclaration.isEffectivelyInternal(): Boolean {
    return isInternal() || parentDeclaration?.isEffectivelyInternal() == true
}
