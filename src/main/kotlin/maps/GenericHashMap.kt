package maps

typealias BucketFactory<K, V> = () -> CustomMutableMap<K, V>

abstract class GenericHashMap<K, V>(val bucketFactory: BucketFactory<K, V>, startingSize: Int, val loadFactor: Double) : CustomMutableMap<K, V> {
    protected var buckets: Array<CustomMutableMap<K, V>> = Array(startingSize) { bucketFactory() }

    private var size = 0

    init {
        if (startingSize and (startingSize - 1) != 0) throw IllegalArgumentException("Please make sure the size is a power of 2")
    }

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

    private fun resize() {
        val newBuckets = Array(buckets.size * 2) { bucketFactory() }
        entries.forEach { newBuckets[hashingFunction(it.key, newBuckets.size)].put(it) }
        buckets = newBuckets
    }

    override fun put(
        key: K,
        value: V,
    ): V? {
        if (size + 1 > buckets.size * loadFactor) {
            resize()
        }

        val bucket = buckets[hashingFunction(key)]
        val removed: V? = bucket.remove(key)
        bucket.put(key, value)
        size++
        return removed
    }

    override fun put(entry: Entry<K, V>): V? = put(entry.key, entry.value)

    override fun remove(key: K): V? {
        val bucket = buckets[hashingFunction(key)]
        if (bucket[key] == null) {
            return null
        }
        size--
        return bucket.remove(key)
    }
}
