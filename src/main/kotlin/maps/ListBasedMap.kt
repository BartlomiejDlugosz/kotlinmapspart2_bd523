package maps

class ListBasedMap<K, V> : CustomMutableMap<K, V> {
    override val entries = CustomLinkedList<Entry<K, V>>()

    override val keys: Iterable<K>
        get() = entries.map { it.key }
    override val values: Iterable<V>
        get() = entries.map { it.value }

    override fun get(key: K): V? = entries.firstOrNull { it.key == key }?.value

    override fun set(
        key: K,
        value: V,
    ): V? = put(key, value)

    override fun put(
        key: K,
        value: V,
    ): V? {
        var removed: V? = null
        if (contains(key)) {
            removed = remove(key)
        }
        entries.add(Entry(key, value))
        return removed
    }

    override fun put(entry: Entry<K, V>): V? = put(entry.key, entry.value)

    override fun remove(key: K): V? {
        val entriesIterator = entries.iterator()
        while (entriesIterator.hasNext()) {
            val entry = entriesIterator.next()
            if (entry.key == key) {
                entriesIterator.remove()
                return entry.value
            }
        }
        return null
    }

    override fun contains(key: K): Boolean = entries.firstOrNull { it.key == key }?.value != null
}
