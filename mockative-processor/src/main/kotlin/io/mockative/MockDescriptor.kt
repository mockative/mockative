package io.mockative

import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier

data class MockDescriptor(
    val packageName: String,
    val name: String,
    val qualifiedName: String,
    val visibility: String?,
    val typeParameters: List<TypeParameter>,

    val mockName: String,

    val properties: List<Property>,
    val functions: List<Function>
) {
    data class Property(
        val isMutable: Boolean,
        val name: String,
        val type: String
    )

    data class Function(
        val isSuspending: Boolean,
        val name: String,
        val typeParameters: List<TypeParameter>,
        val parameters: List<Parameter>,
        val returnType: String,
    ) {
        data class Parameter(
            val modifier: String?,
            val name: String,
            val type: String
        )
    }

    data class TypeParameter(
        val name: String,
        val variance: String,
        val bounds: List<String>
    )
}

fun createMockDescriptor(declaration: KSClassDeclaration): MockDescriptor {
    val packageName = declaration.packageName.asString()
    val name = declaration.simpleName.asString()
    val qualifiedName = declaration.qualifiedName!!.asString()

    val modifiers = declaration.modifiers

    val visibility = when {
        modifiers.contains(Modifier.INTERNAL) -> "internal"
        modifiers.contains(Modifier.PRIVATE) -> "private"
        modifiers.contains(Modifier.PROTECTED) -> "protected"
        modifiers.contains(Modifier.PUBLIC) -> null
        else -> null
    }

    val typeParameters = declaration.typeParameters
        .map { createTypeParameter(it) }

    val mockName = "${name}Mock"

    val properties = declaration.getAllProperties()
        .filter { property -> property.isPublic() }
        .map { property -> createProperty(property) }
        .toList()

    val functions = declaration.getAllFunctions()
        .filter { function -> function.isPublic() && function.isAbstract }
        .map { function -> createFunction(function) }
        .toList()

    return MockDescriptor(
        packageName = packageName,
        name = name,
        qualifiedName = qualifiedName,
        visibility = visibility,
        typeParameters = typeParameters,
        mockName = mockName,
        properties = properties,
        functions = functions
    )
}

fun createProperty(declaration: KSPropertyDeclaration): MockDescriptor.Property {
    val isMutable = declaration.isMutable
    val name = declaration.simpleName.asString()
    val type = declaration.type.resolveUsageSyntax()
    return MockDescriptor.Property(isMutable = isMutable, name = name, type = type)
}

fun createFunction(declaration: KSFunctionDeclaration): MockDescriptor.Function {
    val isSuspending = declaration.modifiers.contains(Modifier.SUSPEND)
    val name = declaration.simpleName.asString()

    val typeParameters = declaration.typeParameters
        .map { typeParameter -> createTypeParameter(typeParameter) }

    val parameters = declaration.parameters
        .map { parameter -> createParameter(parameter) }

    val returnType = declaration.returnType!!.resolveUsageSyntax()

    return MockDescriptor.Function(
        isSuspending = isSuspending,
        name = name,
        typeParameters = typeParameters,
        parameters = parameters,
        returnType = returnType
    )
}

fun createTypeParameter(declaration: KSTypeParameter): MockDescriptor.TypeParameter {
    val name = declaration.name.asString()
    val variance = declaration.variance.label
    val bounds = declaration.bounds
        .map { bound -> bound.resolveUsageSyntax() }
        .toList()

    return MockDescriptor.TypeParameter(name = name, variance = variance, bounds = bounds)
}

fun createParameter(declaration: KSValueParameter): MockDescriptor.Function.Parameter {
    val modifier = if (declaration.isVararg) "vararg" else null
    val name = declaration.name!!.asString()
    val type = declaration.type.resolveUsageSyntax()
    return MockDescriptor.Function.Parameter(modifier = modifier, name = name, type = type)
}

