package io.mockative

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.asClassName

val KOTLIN_THROWS = ClassName("kotlin", "Throws")
val KOTLIN_ANY = ClassName("kotlin", "Any")
val KCLASS = ClassName("kotlin.reflect", "KClass")

val MOCK_ANNOTATION = Mock::class.asClassName()
val MOCKABLE_TYPE_ANNOTATION = MockableType::class.asClassName()

val MOCKABLE = Mockable::class.asClassName()

val MOCKABLE_COMPANION = Mockable.Companion::class.asClassName()
val MOCKABLE_INVOKE = MemberName(MOCKABLE_COMPANION, "invoke")
val MOCKABLE_SUSPEND = MemberName(MOCKABLE_COMPANION, "suspend")

val INVOCATION_GETTER = Invocation.Getter::class.asClassName()
val INVOCATION_SETTER = Invocation.Setter::class.asClassName()
val INVOCATION_FUNCTION = Invocation.Function::class.asClassName()

val LIST_OF = MemberName("kotlin.collections", "listOf")
val EMPTY_LIST = MemberName("kotlin.collections", "emptyListOf")
