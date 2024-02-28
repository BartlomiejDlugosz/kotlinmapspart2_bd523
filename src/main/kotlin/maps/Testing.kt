package maps

fun main() {
    val map = TreeBasedMap<Int, String>(Int::compareTo)
    map.put(5, "5")
    map.put(6, "6")
    map.put(4, "4")
    map.remove(5)
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
    map.entries.sortedBy { it.key.toInt() }.forEach { println(it) }
}
