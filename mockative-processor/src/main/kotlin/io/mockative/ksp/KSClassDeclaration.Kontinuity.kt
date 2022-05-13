package io.mockative.ksp

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile

fun KSClassDeclaration.getAllDependentFiles(): List<KSFile> {
    return listOfNotNull(containingFile) + getAllSuperTypes()
        .mapNotNull { type -> type.declaration as? KSClassDeclaration }
        .mapNotNull { classDec -> classDec.containingFile }
}