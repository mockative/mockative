package io.mockative.ksp

import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile

internal fun FunSpec.Builder.addOriginatingKSFiles(files: Iterable<KSFile>): FunSpec.Builder {
    return files.fold(this) { fileSpec, file ->
        fileSpec.addOriginatingKSFile(file)
    }
}
