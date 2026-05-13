package io.mockative

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName

sealed class ValueOfType {
    data class Concrete(val className: ClassName) : ValueOfType()
    data class SealedLeaf(val sealedClassName: ClassName, val leafClassName: ClassName) : ValueOfType()
    data class NeedsFake(val declaration: KSClassDeclaration, val className: ClassName) : ValueOfType()
}

class NativeMakeValueOfGenerator(
    private val codeGenerator: CodeGenerator,
    private val configuration: MockativeConfiguration,
    private val processableTypes: List<ProcessableType>,
) {
    private val standardTypes = setOf(
        "kotlin.Boolean",
        "kotlin.Byte", "kotlin.Short", "kotlin.Char", "kotlin.Int", "kotlin.Long",
        "kotlin.Float", "kotlin.Double",
        "kotlin.BooleanArray", "kotlin.ByteArray", "kotlin.ShortArray", "kotlin.CharArray",
        "kotlin.IntArray", "kotlin.LongArray", "kotlin.FloatArray", "kotlin.DoubleArray",
        "kotlin.Array",
        "kotlin.collections.List", "kotlin.collections.Map", "kotlin.collections.Set",
        "kotlin.collections.ArrayList", "kotlin.collections.HashMap", "kotlin.collections.HashSet",
        "kotlin.collections.ArrayDeque", "kotlin.collections.LinkedHashMap", "kotlin.collections.LinkedHashSet",
        "kotlin.String", "kotlin.CharSequence",
        "kotlin.Unit", "kotlin.Any", "kotlin.Nothing",
    )

    private val fakePackage = "${PackageResolver.Mockative.resolve()}fake.generated"
    private val makeValueOfPackage = "${PackageResolver.Mockative.resolve()}fake"

    fun generate() {
        val valueOfTypes = collectValueOfTypes()
        val fakeTypes = valueOfTypes.filterIsInstance<ValueOfType.NeedsFake>()

        for (fake in fakeTypes) {
            generateFakeClass(fake)
        }

        generateMakeValueOf(valueOfTypes)
    }

    private fun collectValueOfTypes(): List<ValueOfType> {
        val seen = mutableSetOf<String>()
        val result = mutableListOf<ValueOfType>()

        for (type in processableTypes) {
            for (function in type.declaration.getAllFunctions()) {
                for (param in function.parameters) {
                    val resolved = param.type.resolve()
                    val declaration = resolved.declaration as? KSClassDeclaration ?: continue
                    collectType(declaration, seen, result)
                }
            }

            for (property in type.declaration.getAllProperties()) {
                val resolved = property.type.resolve()
                val declaration = resolved.declaration as? KSClassDeclaration ?: continue
                collectType(declaration, seen, result)
            }
        }

        return result
    }

    private fun collectType(
        declaration: KSClassDeclaration,
        seen: MutableSet<String>,
        result: MutableList<ValueOfType>,
    ) {
        val qualifiedName = declaration.qualifiedName?.asString() ?: return
        if (qualifiedName in standardTypes) return
        if (qualifiedName in seen) return
        if (declaration.typeParameters.isNotEmpty()) return
        seen.add(qualifiedName)

        val className = declaration.toClassName()
        val modifiers = declaration.modifiers

        when {
            modifiers.contains(Modifier.SEALED) -> {
                val leaf = findConcreteLeaf(declaration)
                if (leaf != null) {
                    val leafClassName = leaf.toClassName()
                    result.add(ValueOfType.SealedLeaf(className, leafClassName))
                    val leafQN = leaf.qualifiedName?.asString() ?: ""
                    if (leafQN !in seen) {
                        seen.add(leafQN)
                        result.add(ValueOfType.Concrete(leafClassName))
                    }
                }
            }
            declaration.classKind == ClassKind.INTERFACE -> {
                result.add(ValueOfType.NeedsFake(declaration, className))
            }
            modifiers.contains(Modifier.ABSTRACT) -> {
                result.add(ValueOfType.NeedsFake(declaration, className))
            }
            declaration.classKind == ClassKind.ENUM_CLASS -> {
                result.add(ValueOfType.Concrete(className))
            }
            declaration.classKind == ClassKind.OBJECT -> {
                result.add(ValueOfType.Concrete(className))
            }
            else -> {
                result.add(ValueOfType.Concrete(className))
            }
        }
    }

    private fun findConcreteLeaf(declaration: KSClassDeclaration): KSClassDeclaration? {
        for (subclass in declaration.getSealedSubclasses()) {
            val modifiers = subclass.modifiers
            if (modifiers.contains(Modifier.SEALED)) {
                val leaf = findConcreteLeaf(subclass)
                if (leaf != null) return leaf
            } else if (subclass.classKind == ClassKind.OBJECT) {
                return subclass
            } else if (!modifiers.contains(Modifier.ABSTRACT)) {
                return subclass
            }
        }
        return null
    }

    private fun fakeSimpleName(className: ClassName): String {
        return "Fake__${className.simpleNames.joinToString("_")}"
    }

    private fun generateFakeClass(fake: ValueOfType.NeedsFake) {
        val declaration = fake.declaration
        val className = fake.className
        val simpleName = fakeSimpleName(className)

        val sb = StringBuilder()
        sb.appendLine("@file:Suppress(\"INVISIBLE_MEMBER\", \"INVISIBLE_REFERENCE\")")
        sb.appendLine()
        sb.appendLine("package $fakePackage")
        sb.appendLine()
        sb.appendLine("import ${className.canonicalName}")
        sb.appendLine("import ${makeValueOfPackage}.valueOf")
        sb.appendLine()

        if (declaration.classKind == ClassKind.INTERFACE) {
            sb.appendLine("internal class $simpleName : ${className.simpleName} {")
        } else {
            sb.appendLine("internal class $simpleName : ${className.simpleName}() {")
        }

        for (func in declaration.getDeclaredFunctions()) {
            if (!func.isAbstract) continue
            appendFakeFunction(sb, func)
        }

        for (prop in declaration.getDeclaredProperties()) {
            if (!prop.isAbstract()) continue
            appendFakeProperty(sb, prop)
        }

        sb.appendLine("}")

        val file = codeGenerator.createNewFile(
            dependencies = Dependencies(aggregating = false),
            packageName = fakePackage,
            fileName = simpleName,
        )
        file.write(sb.toString().toByteArray())
        file.close()
    }

    private fun appendFakeFunction(sb: StringBuilder, func: KSFunctionDeclaration) {
        val name = func.simpleName.asString()
        val returnType = func.returnType?.resolve()
        val returnTypeName = returnType?.declaration?.qualifiedName?.asString() ?: "kotlin.Unit"
        val isSuspend = func.modifiers.contains(Modifier.SUSPEND)

        val params = func.parameters.joinToString(", ") { param ->
            val paramName = param.name?.asString() ?: "_"
            val paramType = param.type.resolve().declaration.qualifiedName?.asString() ?: "Any"
            val nullable = if (param.type.resolve().isMarkedNullable) "?" else ""
            "$paramName: $paramType$nullable"
        }

        val suspendModifier = if (isSuspend) "suspend " else ""
        val nullable = if (returnType?.isMarkedNullable == true) "?" else ""

        if (returnTypeName == "kotlin.Unit") {
            sb.appendLine("    override ${suspendModifier}fun $name($params) {}")
        } else {
            sb.appendLine("    @Suppress(\"UNCHECKED_CAST\")")
            sb.appendLine("    override ${suspendModifier}fun $name($params): $returnTypeName$nullable = valueOf<$returnTypeName$nullable>() as $returnTypeName$nullable")
        }
    }

    private fun appendFakeProperty(sb: StringBuilder, prop: KSPropertyDeclaration) {
        val name = prop.simpleName.asString()
        val type = prop.type.resolve()
        val typeName = type.declaration.qualifiedName?.asString() ?: "Any"
        val nullable = if (type.isMarkedNullable) "?" else ""
        sb.appendLine("    @Suppress(\"UNCHECKED_CAST\")")
        sb.appendLine("    override val $name: $typeName$nullable get() = valueOf<$typeName$nullable>() as $typeName$nullable")
    }

    private fun generateMakeValueOf(valueOfTypes: List<ValueOfType>) {
        val sb = StringBuilder()
        sb.appendLine("@file:Suppress(\"INVISIBLE_MEMBER\", \"INVISIBLE_REFERENCE\")")
        sb.appendLine()
        sb.appendLine("package $makeValueOfPackage")
        sb.appendLine()
        sb.appendLine("import kotlin.reflect.KClass")

        // Import all types referenced in the when block
        val imports = mutableSetOf<String>()
        for (type in valueOfTypes) {
            when (type) {
                is ValueOfType.Concrete -> imports.add(type.className.canonicalName)
                is ValueOfType.SealedLeaf -> {
                    imports.add(type.sealedClassName.canonicalName)
                    imports.add(type.leafClassName.canonicalName)
                }
                is ValueOfType.NeedsFake -> {
                    imports.add(type.className.canonicalName)
                    imports.add("$fakePackage.${fakeSimpleName(type.className)}")
                }
            }
        }
        for (import in imports.sorted()) {
            sb.appendLine("import $import")
        }

        sb.appendLine()
        sb.appendLine("@Suppress(\"UNCHECKED_CAST\")")
        sb.appendLine("@OptIn(kotlin.native.internal.InternalForKotlinNative::class)")
        sb.appendLine("internal actual fun <T> makeValueOf(type: KClass<*>): T {")
        sb.appendLine("    return when (type) {")

        for (type in valueOfTypes) {
            when (type) {
                is ValueOfType.Concrete -> {
                    val simple = type.className.simpleName
                    sb.appendLine("        ${simple}::class -> kotlin.native.internal.createUninitializedInstance<${simple}>() as T")
                }
                is ValueOfType.SealedLeaf -> {
                    val sealedSimple = type.sealedClassName.simpleName
                    val leafSimple = type.leafClassName.simpleName
                    sb.appendLine("        ${sealedSimple}::class -> kotlin.native.internal.createUninitializedInstance<${leafSimple}>() as T")
                }
                is ValueOfType.NeedsFake -> {
                    val simple = type.className.simpleName
                    val fakeName = fakeSimpleName(type.className)
                    sb.appendLine("        ${simple}::class -> kotlin.native.internal.createUninitializedInstance<${fakeName}>() as T")
                }
            }
        }

        sb.appendLine("        else -> throw ${PackageResolver.Mockative.resolve()}ValueCreationNotSupportedException(type)")
        sb.appendLine("    }")
        sb.appendLine("}")

        val file = codeGenerator.createNewFile(
            dependencies = Dependencies(aggregating = true),
            packageName = makeValueOfPackage,
            fileName = "MakeValueOf",
        )
        file.write(sb.toString().toByteArray())
        file.close()
    }
}
