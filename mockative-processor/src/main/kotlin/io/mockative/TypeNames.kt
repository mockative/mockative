package io.mockative

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.asClassName

val KOTLIN_THROWS = ClassName("kotlin", "Throws")
val KOTLIN_ANY = ClassName("kotlin", "Any")
val KCLASS = ClassName("kotlin.reflect", "KClass")

val MOCK_ANNOTATION = Mock::class.asClassName()
val SUPPRESS_ANNOTATION = Suppress::class.asClassName()

val MOCKABLE = Mockable::class.asClassName()

val INVOCATION_GETTER = Invocation.Getter::class.asClassName()
val INVOCATION_SETTER = Invocation.Setter::class.asClassName()
val INVOCATION_FUNCTION = Invocation.Function::class.asClassName()

val LIST_OF = MemberName("kotlin.collections", "listOf")
