package maps

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

open class LockedMap<K, V>(private val map: CustomMutableMap<K, V>) : CustomMutableMap<K, V> {
    private val lock: Lock = ReentrantLock()

    override val entries: Iterable<Entry<K, V>>
        get() {
            lock.withLock { return map.entries.toSet() }
        }
    override val keys: Iterable<K>
        get() {
            lock.withLock { return map.keys.toSet() }
        }
    override val values: Iterable<V>
        get() {
            lock.withLock { return map.values.toSet() }
        }

    override fun contains(key: K): Boolean = get(key) != null

    override fun remove(key: K): V? {
        lock.withLock {
            return map.remove(key)
        }
    }

    override fun put(entry: Entry<K, V>): V? = put(entry.key, entry.value)

    override fun put(
        key: K,
        value: V,
    ): V? {
        lock.withLock {
            return map.put(key, value)
        }
    }

    override fun set(
        key: K,
        value: V,
    ): V? = put(key, value)

    override fun get(key: K): V? {
        lock.withLock { return map.get(key) }
    }
}
