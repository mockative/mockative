package io.mockative.kotlin.collections

import io.mockative.Invocation
import io.mockative.MockState
import kotlin.Any
import kotlin.Boolean
import kotlin.IgnorableReturnValue
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.UnsafeVariance
import kotlin.collections.Collection
import kotlin.collections.MutableList
import kotlin.collections.MutableListIterator
import kotlin.collections.listOf

class MutableListMock<E>(private val spyInstance: MutableList<E>? = null) : MutableList<E> {
    override fun add(element: @UnsafeVariance E): Boolean = MockState.invoke<Boolean>(this,
        Invocation.Function("add", listOf<Any?>(`element`)), false, spyInstance?.let { {
            it.`add`(`element`) } })

    override fun add(index: Int, element: @UnsafeVariance E): Unit = MockState.invoke<Unit>(this,
        Invocation.Function("add", listOf<Any?>(`index`, `element`)), true, spyInstance?.let { {
            it.`add`(`index`, `element`) } })

    override fun remove(element: @UnsafeVariance E): Boolean = MockState.invoke<Boolean>(this,
        Invocation.Function("remove", listOf<Any?>(`element`)), false, spyInstance?.let { {
            it.`remove`(`element`) } })

    override fun addAll(elements: Collection<@UnsafeVariance E>): Boolean =
        MockState.invoke<Boolean>(this, Invocation.Function("addAll", listOf<Any?>(`elements`)),
            false, spyInstance?.let { { it.`addAll`(`elements`) } })

    override fun addAll(index: Int, elements: Collection<@UnsafeVariance E>): Boolean =
        MockState.invoke<Boolean>(this, Invocation.Function("addAll", listOf<Any?>(`index`,
            `elements`)), false, spyInstance?.let { { it.`addAll`(`index`, `elements`) } })

    override fun removeAll(elements: Collection<@UnsafeVariance E>): Boolean =
        MockState.invoke<Boolean>(this, Invocation.Function("removeAll", listOf<Any?>(`elements`)),
            false, spyInstance?.let { { it.`removeAll`(`elements`) } })

    override fun retainAll(elements: Collection<@UnsafeVariance E>): Boolean =
        MockState.invoke<Boolean>(this, Invocation.Function("retainAll", listOf<Any?>(`elements`)),
            false, spyInstance?.let { { it.`retainAll`(`elements`) } })

    override fun clear(): Unit = MockState.invoke<Unit>(this, Invocation.Function("clear",
        listOf<Any?>()), true, spyInstance?.let { { it.`clear`() } })

    override fun iterator(): MutableIterator<E> = MockState.invoke<MutableIterator<E>>(this,
        Invocation.Function("iterator", listOf<Any?>()), true, spyInstance?.let { { it.`iterator`() } })

    override fun `set`(index: Int, element: @UnsafeVariance E): @UnsafeVariance E =
        MockState.invoke<E>(this, Invocation.Function("set", listOf<Any?>(`index`, `element`)), false,
            spyInstance?.let { { it.`set`(`index`, `element`) } })

    override fun removeAt(index: Int): @UnsafeVariance E = MockState.invoke<E>(this,
        Invocation.Function("removeAt", listOf<Any?>(`index`)), false, spyInstance?.let { {
            it.`removeAt`(`index`) } })

    override fun listIterator(): MutableListIterator<@UnsafeVariance E> =
        MockState.invoke<MutableListIterator<E>>(this, Invocation.Function("listIterator",
            listOf<Any?>()), false, spyInstance?.let { { it.`listIterator`() } })

    override fun listIterator(index: Int): MutableListIterator<@UnsafeVariance E> =
        MockState.invoke<MutableListIterator<E>>(this, Invocation.Function("listIterator",
            listOf<Any?>(`index`)), false, spyInstance?.let { { it.`listIterator`(`index`) } })

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<@UnsafeVariance E> =
        MockState.invoke<MutableList<E>>(this, Invocation.Function("subList",
            listOf<Any?>(`fromIndex`, `toIndex`)), false, spyInstance?.let { { it.`subList`(`fromIndex`,
            `toIndex`) } })

    override val size: Int
        get() = MockState.invoke<Int>(this, Invocation.Getter("size"), false, spyInstance?.let { {
            it.`size` } })

    override fun contains(element: E): Boolean = MockState.invoke<Boolean>(this, Invocation.Function("contains",
        listOf<Any?>(`element`)), false, spyInstance?.let { { it.`contains`(`element`) } })

    override fun containsAll(elements: Collection<E>): Boolean = MockState.invoke<Boolean>(this,
        Invocation.Function("containsAll", listOf<Any?>(`elements`)), false,
        spyInstance?.let { { it.`containsAll`(`elements`) } })

    override fun `get`(index: Int): @UnsafeVariance E = MockState.invoke<E>(this,
        Invocation.Function("get", listOf<Any?>(`index`)), false, spyInstance?.let { {
            it.`get`(`index`) } })

    override fun indexOf(element: @UnsafeVariance E): Int = MockState.invoke<Int>(this,
        Invocation.Function("indexOf", listOf<Any?>(`element`)), false, spyInstance?.let { {
            it.`indexOf`(`element`) } })

    override fun isEmpty(): Boolean = MockState.invoke<Boolean>(this, Invocation.Function("isEmpty",
        listOf<Any?>()), false, spyInstance?.let { { it.`isEmpty`() } })

    override fun lastIndexOf(element: @UnsafeVariance E): Int = MockState.invoke<Int>(this,
        Invocation.Function("lastIndexOf", listOf<Any?>(`element`)), false, spyInstance?.let { {
            it.`lastIndexOf`(`element`) } })

    override fun equals(other: Any?): Boolean = MockState.invoke<Boolean>(this,
        Invocation.Function("equals", listOf<Any?>(`other`)), { super.equals(`other`) },
        spyInstance?.let { { it.`equals`(`other`) } })

    override fun hashCode(): Int = MockState.invoke<Int>(this, Invocation.Function("hashCode",
        listOf<Any?>()), { super.hashCode() }, spyInstance?.let { { it.`hashCode`() } })

    override fun toString(): String = MockState.invoke<String>(this, Invocation.Function("toString",
        listOf<Any?>()), { super.toString() }, spyInstance?.let { { it.`toString`() } })
}
