package io.mockative

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName

data class ProcessableFile(
    val declaration: KSFile,
    val packageName: String,
    val fileName: String,
    val types: List<ProcessableType>,
) {
    companion object {
        @OptIn(KotlinPoetKspPreview::class)
        fun fromResolver(resolver: Resolver): List<ProcessableFile> {
            return ProcessableType.fromResolver(resolver)
                .groupBy { it.declaration.containingFile ?: throw Error("The declaration ${it.declaration.toClassName()} has no containing file") }
                .map { (file, types) ->
                    ProcessableFile(
                        declaration = file,
                        packageName = file.packageName.asString(),
                        fileName = file.fileName,
                        types = types,
                    )
                }
        }
    }
}