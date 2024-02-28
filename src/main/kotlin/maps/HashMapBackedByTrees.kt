package maps

class HashMapBackedByTrees<K, V>(comparator: Comparator<K>, size: Int = 32, loadFactor: Double = 0.75) : GenericHashMap<K, V>(
    { TreeBasedMap(comparator) },
    size,
    loadFactor,
)
