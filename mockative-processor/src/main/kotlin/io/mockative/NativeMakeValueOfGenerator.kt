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
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo

sealed class ValueOfType {
    data class Concrete(val className: ClassName) : ValueOfType()
    data class SealedLeaf(val sealedClassName: ClassName, val leafClassName: ClassName) : ValueOfType()
    data class NeedsFake(val declaration: KSClassDeclaration, val className: ClassName) : ValueOfType()
    data class FunctionType(val arity: Int, val isSuspend: Boolean) : ValueOfType()
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

    private val valueOfMember = MemberName(makeValueOfPackage, "valueOf")
    private val valueCreationNotSupportedException =
        ClassName(PackageResolver.Mockative.resolve().removeSuffix("."), "ValueCreationNotSupportedException")

    private val unsafeCreateMember = MemberName("io.mockative.fake", "unsafeCreateUninitializedInstance")

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

    private val functionTypePattern = Regex("""^kotlin\.Function(\d+)$""")
    private val suspendFunctionTypePattern = Regex("""^kotlin\.coroutines\.SuspendFunction(\d+)$""")

    private fun collectType(
        declaration: KSClassDeclaration,
        seen: MutableSet<String>,
        result: MutableList<ValueOfType>,
    ) {
        val qualifiedName = declaration.qualifiedName?.asString() ?: return
        if (qualifiedName in standardTypes) return
        if (qualifiedName in seen) return

        functionTypePattern.matchEntire(qualifiedName)?.let { match ->
            seen.add(qualifiedName)
            result.add(ValueOfType.FunctionType(match.groupValues[1].toInt(), isSuspend = false))
            return
        }
        suspendFunctionTypePattern.matchEntire(qualifiedName)?.let { match ->
            seen.add(qualifiedName)
            result.add(ValueOfType.FunctionType(match.groupValues[1].toInt(), isSuspend = true))
            return
        }

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
                    collectIntermediateSealedTypes(declaration, seen, result)
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

    private fun collectIntermediateSealedTypes(
        declaration: KSClassDeclaration,
        seen: MutableSet<String>,
        result: MutableList<ValueOfType>,
    ) {
        for (subclass in declaration.getSealedSubclasses()) {
            val subQN = subclass.qualifiedName?.asString() ?: continue
            if (subQN in seen) continue
            if (subclass.modifiers.contains(Modifier.SEALED)) {
                val leaf = findConcreteLeaf(subclass)
                if (leaf != null) {
                    seen.add(subQN)
                    result.add(ValueOfType.SealedLeaf(subclass.toClassName(), leaf.toClassName()))
                    collectIntermediateSealedTypes(subclass, seen, result)
                }
            }
        }
    }

    private fun buildFunctionLiteral(arity: Int, isSuspend: Boolean): String {
        val params = if (arity == 0) "" else (0 until arity).joinToString(", ") { "_: Any?" } + " -> "
        val body = "throw UnsupportedOperationException()"
        val lambda = "{ $params$body }"
        return if (isSuspend) "suspend $lambda" else lambda
    }

    private fun fakeSimpleName(className: ClassName): String {
        return "Fake__${className.simpleNames.joinToString("_")}"
    }

    private fun fakeClassName(className: ClassName): ClassName {
        return ClassName(fakePackage, fakeSimpleName(className))
    }

    private fun generateFakeClass(fake: ValueOfType.NeedsFake) {
        val declaration = fake.declaration
        val className = fake.className
        val fakeName = fakeSimpleName(className)

        val typeSpecBuilder = TypeSpec.classBuilder(fakeName)
            .addModifiers(KModifier.INTERNAL)

        if (declaration.classKind == ClassKind.INTERFACE) {
            typeSpecBuilder.addSuperinterface(className)
        } else {
            typeSpecBuilder.superclass(className)
            val primaryConstructor = declaration.primaryConstructor
            if (primaryConstructor != null && primaryConstructor.parameters.isNotEmpty()) {
                val codeArgs = mutableListOf<Any>()
                val formatParts = primaryConstructor.parameters.map { param ->
                    val paramType = param.type.toTypeName()
                    codeArgs.add(valueOfMember)
                    codeArgs.add(paramType)
                    codeArgs.add(paramType)
                    "%M<%T>() as %T"
                }
                typeSpecBuilder.addSuperclassConstructorParameter(
                    CodeBlock.of(formatParts.joinToString(", "), *codeArgs.toTypedArray())
                )
            }
        }

        for (func in declaration.getDeclaredFunctions()) {
            if (!func.isAbstract) continue
            typeSpecBuilder.addFunction(buildFakeFunction(func))
        }

        for (prop in declaration.getDeclaredProperties()) {
            if (!prop.isAbstract()) continue
            typeSpecBuilder.addProperty(buildFakeProperty(prop))
        }

        val fileSpec = FileSpec.builder(fakePackage, fakeName)
            .addType(typeSpecBuilder.build())
            .build()

        fileSpec.writeTo(codeGenerator, Dependencies(aggregating = false))
    }

    private fun buildFakeFunction(func: KSFunctionDeclaration): FunSpec {
        val name = func.simpleName.asString()
        val returnType = func.returnType?.resolve()
        val returnTypeName = returnType?.declaration?.qualifiedName?.asString() ?: "kotlin.Unit"
        val isSuspend = func.modifiers.contains(Modifier.SUSPEND)

        val builder = FunSpec.builder(name)
            .addModifiers(KModifier.OVERRIDE)

        if (isSuspend) {
            builder.addModifiers(KModifier.SUSPEND)
        }

        for (param in func.parameters) {
            val paramName = param.name?.asString() ?: "_"
            val paramType = param.type.toTypeName()
            builder.addParameter(paramName, paramType)
        }

        if (returnTypeName != "kotlin.Unit") {
            val resolvedReturnType = func.returnType!!.toTypeName()
            builder.returns(resolvedReturnType)
            builder.addAnnotation(
                AnnotationSpec.builder(SUPPRESS_ANNOTATION)
                    .addMember("%S", "UNCHECKED_CAST")
                    .build()
            )
            builder.addStatement(
                "return %M<%T>() as %T",
                valueOfMember,
                resolvedReturnType,
                resolvedReturnType,
            )
        }

        return builder.build()
    }

    private fun buildFakeProperty(prop: KSPropertyDeclaration): PropertySpec {
        val name = prop.simpleName.asString()
        val typeName = prop.type.toTypeName()

        return PropertySpec.builder(name, typeName)
            .addModifiers(KModifier.OVERRIDE)
            .addAnnotation(
                AnnotationSpec.builder(SUPPRESS_ANNOTATION)
                    .addMember("%S", "UNCHECKED_CAST")
                    .build()
            )
            .getter(
                FunSpec.getterBuilder()
                    .addStatement("return %M<%T>() as %T", valueOfMember, typeName, typeName)
                    .build()
            )
            .build()
    }

    private fun generateMakeValueOf(valueOfTypes: List<ValueOfType>) {
        val internalMockativeApi = ClassName("io.mockative", "InternalMockativeApi")

        val funBuilder = FunSpec.builder("makeValueOf")
            .addModifiers(KModifier.INTERNAL, KModifier.ACTUAL)
            .addTypeVariable(TypeVariableName("T"))
            .addParameter("type", KCLASS.parameterizedBy(STAR))
            .returns(TypeVariableName("T"))
            .addAnnotation(
                AnnotationSpec.builder(SUPPRESS_ANNOTATION)
                    .addMember("%S", "UNCHECKED_CAST")
                    .build()
            )
            .addAnnotation(
                AnnotationSpec.builder(OPT_IN)
                    .addMember("%T::class", internalMockativeApi)
                    .build()
            )

        val whenBlock = CodeBlock.builder()
            .beginControlFlow("return when (type)")

        for (type in valueOfTypes) {
            when (type) {
                is ValueOfType.Concrete -> {
                    whenBlock.addStatement(
                        "%T::class -> %M<%T>() as T",
                        type.className,
                        unsafeCreateMember,
                        type.className,
                    )
                }
                is ValueOfType.SealedLeaf -> {
                    whenBlock.addStatement(
                        "%T::class -> %M<%T>() as T",
                        type.sealedClassName,
                        unsafeCreateMember,
                        type.leafClassName,
                    )
                }
                is ValueOfType.NeedsFake -> {
                    whenBlock.addStatement(
                        "%T::class -> %M<%T>() as T",
                        type.className,
                        unsafeCreateMember,
                        fakeClassName(type.className),
                    )
                }
                is ValueOfType.FunctionType -> {
                    val klassName = if (type.isSuspend) {
                        ClassName("kotlin.coroutines", "SuspendFunction${type.arity}")
                    } else {
                        ClassName("kotlin", "Function${type.arity}")
                    }
                    val lambda = buildFunctionLiteral(type.arity, type.isSuspend)
                    whenBlock.addStatement(
                        "%T::class -> ($lambda) as T",
                        klassName,
                    )
                }
            }
        }

        whenBlock.addStatement("else -> throw %T(type)", valueCreationNotSupportedException)
        whenBlock.endControlFlow()

        funBuilder.addCode(whenBlock.build())

        val fileSpec = FileSpec.builder(makeValueOfPackage, "MakeValueOf")
            .addFunction(funBuilder.build())
            .build()

        fileSpec.writeTo(codeGenerator, Dependencies(aggregating = true))
    }
}

