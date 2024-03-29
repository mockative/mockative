package io.mockative.ksp

import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile

internal fun TypeSpec.Builder.addOriginatingKSFiles(files: Iterable<KSFile>): TypeSpec.Builder {
    return files.fold(this) { fileSpec, file ->
        fileSpec.addOriginatingKSFile(file)
    }
}
