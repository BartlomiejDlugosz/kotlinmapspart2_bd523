package maps

class testing {
}

fun main() {
    val map = TreeBasedMap<Int, String>(Int::compareTo)
    map.put(15, "15")
    map.put(14, "14")
    map.put(17, "17")
    map.put(16, "16")
    map.remove(16)
    map.entries.forEach { println(it) }
}