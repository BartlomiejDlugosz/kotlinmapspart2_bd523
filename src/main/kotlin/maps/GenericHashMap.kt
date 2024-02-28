package maps

typealias BucketFactory<K, V> = () -> CustomMutableMap<K, V>

abstract class GenericHashMap<K, V> : CustomMutableMap<K, V> {
    abstract val bucketFactory: BucketFactory<K, V>
    abstract var buckets: Array<CustomMutableMap<K, V>>

    private var numberOfEntries = 0

    abstract val size: Int
    abstract val loadFactor: Double

    private fun hashingFunction(
        key: K,
        size: Int = buckets.size,
    ): Int = key.hashCode() and (size - 1)

    override val entries: Iterable<Entry<K, V>>
        get() = buckets.flatMap { it.entries }

    override val keys: Iterable<K>
        get() = buckets.flatMap { it.keys }
    override val values: Iterable<V>
        get() = buckets.flatMap { it.values }

    override fun contains(key: K): Boolean = get(key) != null

    override fun get(key: K): V? = buckets[hashingFunction(key)][key]

    override fun set(
        key: K,
        value: V,
    ): V? = put(key, value)

    override fun put(
        key: K,
        value: V,
    ): V? {
        if (numberOfEntries + 1 > buckets.size * loadFactor) {
            val newBuckets = Array(buckets.size * 2) { bucketFactory() }
            entries.forEach { newBuckets[hashingFunction(it.key, newBuckets.size)].put(it) }
            buckets = newBuckets
        }

        val bucket = buckets[hashingFunction(key)]
        if (bucket[key] == null) {
            bucket.put(key, value)
            numberOfEntries++
            return null
        }
        val removed = bucket.remove(key)
        bucket.put(key, value)
        numberOfEntries++
        return removed
    }

    override fun put(entry: Entry<K, V>): V? = put(entry.key, entry.value)

    override fun remove(key: K): V? {
        val bucket = buckets[hashingFunction(key)] ?: return null
        if (bucket[key] == null) {
            return null
        }
        numberOfEntries--
        return bucket.remove(key)
    }
}
