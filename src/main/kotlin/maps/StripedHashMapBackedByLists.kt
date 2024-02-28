package maps

class StripedHashMapBackedByLists<K, V>(size: Int = 32, loadFactor: Double = 0.75) : StripedGenericHashMap<K, V>(size, loadFactor) {
    override val bucketFactory: BucketFactory<K, V> = { ListBasedMap() }
    override var buckets: Array<CustomMutableMap<K, V>> = Array(size) { bucketFactory() }

    init {
        if (size and (size - 1) != 0) throw IllegalArgumentException("Please make sure the size is a power of 2")
    }
}

class StripedHashMapBackedByTrees<K, V>(comparator: Comparator<K>, size: Int = 32, loadFactor: Double = 0.75) : StripedGenericHashMap<K, V>(
    size,
    loadFactor,
) {
    override val bucketFactory: BucketFactory<K, V> = { TreeBasedMap(comparator) }
    override var buckets: Array<CustomMutableMap<K, V>> = Array(size) { bucketFactory() }

    init {
        if (size and (size - 1) != 0) throw IllegalArgumentException("Please make sure the size is a power of 2")
    }
}
