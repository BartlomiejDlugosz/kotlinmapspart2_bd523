package maps

import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

// val NUM_THREADS = Runtime.getRuntime().availableProcessors()
const val NUM_THREADS = 6
const val NUM_OPERATIONS = 5_000_000
const val REMOVE_PROBABILITY = 0.25

fun main() {
    val maxMemory = Runtime.getRuntime().maxMemory()
    println("Max Memory: " + (maxMemory / 1024 / 1024) + " MB")

    benchmark("Global Lock HashMap - List") { LockedHashMapBackedByLists<String, Int>() }
    benchmark("Striped HashMap - List") { StripedHashMapBackedByLists<String, Int>() }

    benchmark("Global Lock HashMap - Trees") { LockedHashMapBackedByTrees<String, Int>(String::compareTo) }
    benchmark("Striped HashMap - Trees") { StripedHashMapBackedByTrees<String, Int>(String::compareTo) }
}

fun benchmark(
    mapType: String,
    factory: () -> CustomMutableMap<String, Int>,
) {
    val map = factory()
    val counter = AtomicInteger()
    val threads = mutableListOf<Thread>()
    val rnd = Random()

    val time =
        measureTimeMillis {
            repeat(NUM_THREADS) {
                threads.add(
                    thread {
                        repeat(NUM_OPERATIONS) {
                            val key = "key-${rnd.nextInt(10_000_000)}"

                            if (rnd.nextDouble() < REMOVE_PROBABILITY) {
                                map.remove(key)
                            } else {
                                map[key] = (map[key] ?: 0) + 1
                            }
                            counter.incrementAndGet()
                        }
                    },
                )
            }

            threads.forEach { it.join() }
        }

    println("$mapType - Operations: ${counter.get()}")
    println("$mapType - Time (ms): $time")
}
