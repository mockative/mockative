package io.mockative.ksp

import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.addOriginatingKSFile

@OptIn(KotlinPoetKspPreview::class)
internal fun TypeSpec.Builder.addOriginatingKSFiles(files: Iterable<KSFile>): TypeSpec.Builder {
    return files.fold(this) { fileSpec, file ->
        fileSpec.addOriginatingKSFile(file)
    }
}

internal fun TypeSpec.Builder.addEmptyConstructor(): TypeSpec.Builder {
    return addFunction(FunSpec.constructorBuilder().build())
}
