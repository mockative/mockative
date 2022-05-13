package io.mockative.ksp

import com.squareup.kotlinpoet.FileSpec
import io.mockative.ProcessableType
import io.mockative.kotlinpoet.buildMockTypeSpec

fun FileSpec.Builder.addMocks(types: List<ProcessableType>): FileSpec.Builder {
    return types.fold(this) { fileSpec, type ->
        fileSpec.addMock(type)
    }
}

private fun FileSpec.Builder.addMock(type: ProcessableType): FileSpec.Builder {
    return addType(type.buildMockTypeSpec())
}
