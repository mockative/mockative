package io.mockative

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.asClassName

val KOTLIN_THROWS = ClassName("kotlin", "Throws")
val KOTLIN_ANY = ClassName("kotlin", "Any")
val KCLASS = ClassName("kotlin.reflect", "KClass")

val MOCKABLE_ANNOTATION = Mockable::class.asClassName()
val SUPPRESS_ANNOTATION = Suppress::class.asClassName()
val HIDDEN_FROM_OBJC_ANNOTATION = ClassName("kotlin.native", "HiddenFromObjC")
val OPT_IN = ClassName("kotlin", "OptIn")
val DEPRECATED_ANNOTATION = Deprecated::class.asClassName()

val MOCK_STATE = ClassName("io.mockative", "MockState")

val INVOCATION_GETTER = ClassName("io.mockative", "Invocation", "Getter")
val INVOCATION_SETTER = ClassName("io.mockative", "Invocation", "Setter")
val INVOCATION_FUNCTION = ClassName("io.mockative", "Invocation", "Function")

val LIST_OF = MemberName("kotlin.collections", "listOf")

val CONFIGURE = MemberName("io.mockative", "configure")

val ARRAY_LIST = ClassName("kotlin.collections", "ArrayList")
val ARRAY_DEQUE = ClassName("kotlin.collections", "ArrayDeque")
val LINKED_HASH_MAP = ClassName("kotlin.collections", "LinkedHashMap")
val HASH_MAP = ClassName("kotlin.collections", "HashMap")
val LINKED_HASH_SET = ClassName("kotlin.collections", "LinkedHashSet")
val HASH_SET = ClassName("kotlin.collections", "HashSet")
