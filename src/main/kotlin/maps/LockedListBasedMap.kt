package maps

class LockedListBasedMap<K, V> : LockedMap<K, V>(ListBasedMap<K, V>())

class LockedHashMapBackedByLists<K, V> : LockedMap<K, V>(HashMapBackedByLists<K, V>())