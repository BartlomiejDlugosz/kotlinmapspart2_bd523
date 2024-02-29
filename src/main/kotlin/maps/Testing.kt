package maps

import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.concurrent.thread
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

//fun main() {
//    val map = TreeBasedMap<Int, String>(Int::compareTo)
//    map.put(5, "5")
//    map.put(6, "6")
//    map.put(4, "4")
//    map.remove(5)
//    val map = HashMapBackedByTrees<String, Int>(String::compareTo)
//    for (i in 1..100) {
//        map.put(Entry(i.toString(), i))
//    }
//    for (i in 1..100) {
//        if (i != map[i.toString()]) println("1: ${i}")
//        if (i % 2 == 0) {
//            map.remove(i.toString())
//        }
//    }
//    for (i in 1..100) {
//        if (i % 4 == 0) {
//            assertNull(map.get(i.toString()))
//            assertFalse(map.contains(i.toString()))
//            assertNull(map.set(i.toString(), i))
//        }
//    }
//    map.entries.sortedBy { it.key.toInt() }.forEach { println(it) }
//    println("15".hashCode() and (16 - 1))
//    println("15".hashCode() and (32 - 1))
//    println("15".hashCode() and (64 - 1))
//    println("15".hashCode() and (128 - 1))
//    println("15".hashCode() and (256 - 1))
//    println("15".hashCode() and (512 - 1))
//}

fun main() {
  val rnd = Random()

  val globalLockMap = LockedHashMapBackedByLists<String, Int>()
  val stripedMap = StripedHashMapBackedByTrees<String, Int>(String::compareTo)
  val stack = ConcurrentLinkedQueue<Entry<String, Int>>()

  val currentProcess = Runtime.getRuntime().availableProcessors()

  var threads = mutableListOf<Thread>()
  for (i in 1..currentProcess) {
    threads.add(thread{
      for (j in 1..1000000) {
        val newNum = rnd.nextInt(1000000)
        stack.add(Entry(newNum.toString(), newNum))
      }
    })
  }
  threads.forEach { it.join() }
  threads = mutableListOf()

  val timeForGlobalLock = measureTimeMillis {
    for (i in 1..currentProcess) {
      threads.add(thread{
        globalLockMap.put(stack.poll())
      })
    }
    threads.forEach { it.join() }
  }
  println(timeForGlobalLock)

  val timeForStripedLock = measureTimeMillis {
    for (i in 1..currentProcess) {
      threads.add(thread{
        stripedMap.put(stack.poll())
      })
    }
    threads.forEach { it.join() }
  }

  println(timeForStripedLock)
}