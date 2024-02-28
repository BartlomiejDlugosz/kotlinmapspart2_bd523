package maps

class LockedListBasedMap<K, V> : LockedMap<K, V>(ListBasedMap())

class LockedHashMapBackedByLists<K, V> : LockedMap<K, V>(HashMapBackedByLists())