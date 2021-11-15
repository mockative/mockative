package io.mockative.concurrency

/**
 * An atomic, thread-sharable generic ordered collection of elements.
 */
internal class AtomicList<E> : List<E> {
    private val ref: AtomicRef<List<E>> = AtomicRef(emptyList())

    private inline fun <R> mutate(block: ArrayList<E>.() -> R): R {
        while (true) {
            val currentValue = ref.value

            val nextValue = ArrayList<E>(currentValue.size + 1)
            nextValue.addAll(currentValue)
            val result = block(nextValue)

            if (ref.compareAndSet(currentValue, nextValue)) {
                return result
            }
        }
    }

    override val size: Int
        get() = ref.value.size

    override fun contains(element: E): Boolean = ref.value.contains(element)
    override fun containsAll(elements: Collection<E>): Boolean = ref.value.containsAll(elements)
    override fun get(index: Int): E = ref.value[index]
    override fun indexOf(element: E): Int = ref.value.indexOf(element)
    override fun isEmpty(): Boolean = ref.value.isEmpty()
    override fun iterator(): Iterator<E> = ref.value.iterator()
    override fun lastIndexOf(element: E): Int = ref.value.lastIndexOf(element)

    fun add(element: E): Boolean = mutate { add(element) }
    fun add(index: Int, element: E) = mutate { add(index, element) }
    fun addAll(index: Int, elements: Collection<E>): Boolean = mutate { addAll(index, elements) }
    fun addAll(elements: Collection<E>): Boolean = mutate { addAll(elements) }

    fun clear() = mutate { clear() }

    override fun listIterator(): ListIterator<E> = ref.value.listIterator()
    override fun listIterator(index: Int): ListIterator<E> = ref.value.listIterator()

    fun remove(element: E): Boolean = mutate { remove(element) }
    fun removeAll(elements: Collection<E>): Boolean = mutate { removeAll(elements) }
    fun removeAt(index: Int): E = mutate { removeAt(index) }

    fun retainAll(elements: Collection<E>): Boolean = mutate { retainAll(elements) }

    fun set(index: Int, element: E): E = mutate { set(index, element) }

    override fun subList(fromIndex: Int, toIndex: Int): List<E> = ref.value.subList(fromIndex, toIndex)
}
