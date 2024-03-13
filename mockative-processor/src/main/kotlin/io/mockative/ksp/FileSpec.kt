package io.mockative.ksp

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec

fun FileSpec.Builder.addFunctions(functions: Iterable<FunSpec>): FileSpec.Builder {
    return functions.fold(this) { fileSpec, function ->
        fileSpec.addFunction(function)
    }
}