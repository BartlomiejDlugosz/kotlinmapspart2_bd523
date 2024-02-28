package maps

class HashMapBackedByLists<K, V>(override val size: Int = 32, override val loadFactor: Double = 0.75) : GenericHashMap<K, V>() {
    override val bucketFactory: BucketFactory<K, V> = { ListBasedMap() }
    override var buckets: Array<CustomMutableMap<K, V>> = Array(size) { bucketFactory() }

    init {
        if (size and (size - 1) != 0) throw IllegalArgumentException("Please make sure the size is a power of 2")
    }
}
