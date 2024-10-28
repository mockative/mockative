package io.mockative.kotlinpoet

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName

internal fun MemberName.Companion.bestGuess(fqn: String): MemberName {
    val indexOfLastSeparator = fqn.lastIndexOf('.')
    require(indexOfLastSeparator > -1) { "Could not resolve member name of '$fqn'. No separator '.' found." }

    val classNameString = fqn.substring(0, indexOfLastSeparator)
    val memberName = fqn.substring(indexOfLastSeparator + 1)

    return MemberName(ClassName.bestGuess(classNameString), memberName)
}
