package maps

class HashMapBackedByTrees<K, V>(comparator: Comparator<K>, bucketFactory: BucketFactory<K, V> = {TreeBasedMap<K, V>(comparator)}, override val size: Int = 16, override val loadFactor: Double = 0.75): GenericHashMap<K, V>(bucketFactory) {
  override var buckets: Array<CustomMutableMap<K, V>> = Array(size) {bucketFactory()}

  init {
    if (size and (size - 1) != 0) throw IllegalArgumentException("Please make sure the size is a power of 2")
  }
}