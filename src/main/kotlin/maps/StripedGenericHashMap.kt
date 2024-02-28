package maps

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

abstract class StripedGenericHashMap<K, V>(bucketFactory: BucketFactory<K, V>, size: Int = 32, loadFactor: Double = 0.75) : GenericHashMap<K, V>(
    bucketFactory,
    size,
    loadFactor,
) {
    private var numberOfEntries: AtomicInteger = AtomicInteger(0)

    private val locks: List<Lock> = List(size) { ReentrantLock() }

    private fun hashingFunction(
        key: K,
        size: Int = buckets.size,
    ): Int = key.hashCode() and (size - 1)

    override val entries: Iterable<Entry<K, V>>
        get() {
            try {
                locks.forEach { it.lock() }
                return buckets.flatMap { it.entries }
            } finally {
                locks.forEach { it.unlock() }
            }
        }

    override val keys: Iterable<K>
        get() {
            try {
                locks.forEach { it.lock() }
                return buckets.flatMap { it.keys }
            } finally {
                locks.forEach { it.unlock() }
            }
        }
    override val values: Iterable<V>
        get() {
            try {
                locks.forEach { it.lock() }
                return buckets.flatMap { it.values }
            } finally {
                locks.forEach { it.unlock() }
            }
        }

    private fun resize() {
        try {
            locks.forEach { it.lock() }
            if (numberOfEntries.get() + 1 > buckets.size * loadFactor) {
                val newBuckets = Array(buckets.size * 2) { bucketFactory() }
                entries.forEach { newBuckets[hashingFunction(it.key, newBuckets.size)].put(it) }
                buckets = newBuckets
            }
        } finally {
            locks.forEach { it.unlock() }
        }
    }

    override fun contains(key: K): Boolean = get(key) != null

    override fun get(key: K): V? {
        locks[hashingFunction(key) % locks.size].withLock {
            return buckets[hashingFunction(key)][key]
        }
    }

    override fun set(
        key: K,
        value: V,
    ): V? = put(key, value)

    override fun put(
        key: K,
        value: V,
    ): V? {
        if (numberOfEntries.get() + 1 > buckets.size * loadFactor) {
            resize()
        }
        val hash = hashingFunction(key)
        locks[hash % locks.size].withLock {
            val bucket = buckets[hash]
            val removed = bucket.remove(key)
            bucket.put(key, value)
            numberOfEntries.incrementAndGet()
            return removed
        }
    }

    override fun put(entry: Entry<K, V>): V? = put(entry.key, entry.value)

    override fun remove(key: K): V? {
        locks[hashingFunction(key) and (locks.size - 1)].withLock {
            numberOfEntries.decrementAndGet()
            return buckets[hashingFunction(key)].remove(key)
        }
    }
}
