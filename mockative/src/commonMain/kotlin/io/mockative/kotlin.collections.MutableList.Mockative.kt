@file:Suppress(
    "DEPRECATION",
    "DEPRECATION_ERROR",
    "all",
)

package io.mockative

import io.mockative.kotlin.collections.MutableListMock
import kotlin.Suppress
import kotlin.collections.MutableList
import kotlin.reflect.KClass

public fun <E> mock(@Suppress("UNUSED_PARAMETER") type: KClass<MutableList<E>>): MutableList<E> =
    configure(MutableListMock<E>(null)) { stubsUnitByDefault = true }

public fun <E> spy(@Suppress("UNUSED_PARAMETER") type: KClass<MutableList<E>>, on: MutableList<E>):
        MutableList<E> = configure(MutableListMock<E>(on)) { stubsUnitByDefault = true }

public fun <E> spyOn(on: MutableList<E>): MutableList<E> = configure(MutableListMock<E>(on)) {
    stubsUnitByDefault = true }

public fun <E> any(@Suppress("UNUSED_PARAMETER") type: KClass<MutableList<E>>): MutableList<E> =
    configure(MutableListMock<E>(null)) { stubsUnitByDefault = true }
