package maps

class HashMapBackedByLists<K, V>(size: Int = 32, loadFactor: Double = 0.75) : GenericHashMap<K, V>({ ListBasedMap() }, size, loadFactor)
