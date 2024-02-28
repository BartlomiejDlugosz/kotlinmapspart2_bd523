package maps

class StripedHashMapBackedByLists<K, V>(size: Int = 32, loadFactor: Double = 0.75) : StripedGenericHashMap<K, V>({
    ListBasedMap()
}, size, loadFactor)

class StripedHashMapBackedByTrees<K, V>(comparator: Comparator<K>, size: Int = 32, loadFactor: Double = 0.75) : StripedGenericHashMap<K, V>(
    { TreeBasedMap(comparator) },
    size,
    loadFactor,
)
