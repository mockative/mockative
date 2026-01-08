package io.mockative.concurrency

/**
 * An atomic, thread-sharable generic unordered collection of elements that does not support
 * duplicate elements.
 */
internal class AtomicSet<E> : Set<E> {
    private val ref: AtomicRef<Set<E>> = AtomicRef(emptySet())

    private inline fun <R> mutate(block: HashSet<E>.() -> R): R {
        while (true) {
            val currentValue = ref.value

            val nextValue = HashSet<E>(currentValue.size + 1)
            nextValue.addAll(currentValue)
            val result = block(nextValue)

            if (ref.compareAndSet(currentValue, nextValue)) {
                return result
            }
        }
    }

    fun add(element: E): Boolean = mutate { add(element) }
    fun addAll(elements: Collection<E>): Boolean = mutate { addAll(elements) }

    fun clear() = mutate { clear() }

    override fun iterator(): Iterator<E> = ref.value.iterator()

    fun remove(element: E): Boolean = mutate { remove(element) }
    fun removeAll(elements: Collection<E>): Boolean = mutate { removeAll(elements) }

    fun retainAll(elements: Collection<E>): Boolean = mutate { retainAll(elements) }

    override val size: Int
        get() = ref.value.size

    override fun contains(element: E): Boolean = ref.value.contains(element)
    override fun containsAll(elements: Collection<E>): Boolean = ref.value.containsAll(elements)

    override fun isEmpty(): Boolean = ref.value.isEmpty()
}
