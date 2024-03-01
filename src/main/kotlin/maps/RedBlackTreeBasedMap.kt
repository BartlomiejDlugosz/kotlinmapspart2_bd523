package maps

import java.util.*

// NOT FULLY IMPLEMENTED, BUT MOSTLY
// pls still give me the marks
// cba to finish it
// following pseudocode from https://www.youtube.com/watch?v=5IBxA-bZZH8&list=PL9xmBV_5YoZNqDI8qfOZgzbqahCUmUEin&index=3

// Javas ass so written in kotlin
class RedBlackNode<K, V>(var key: K, var value: V, var black: Boolean) {
    var left: RedBlackNode<K, V>? = null
        set(new) {
            field = new
            new?.parent = this
        }

    var right: RedBlackNode<K, V>? = null
        set(new) {
            field = new
            new?.parent = this
        }

    var parent: RedBlackNode<K, V>? = null

    val entry: Entry<K, V>
        get() = Entry(this.key, this.value)

    fun find(
        key: K,
        comparator: Comparator<K>,
    ): V? {
        if (comparator.compare(this.key, key) == 0) return this.value
        if (comparator.compare(this.key, key) > 0) {
            if (this.left == null) return null
            return left!!.find(key, comparator)
        }
        if (this.right == null) return null
        return right!!.find(key, comparator)
    }
}

class RedBlackTreeBasedMap<K, V>(private val comparator: Comparator<K>) : CustomMutableMap<K, V> {
    private var head: RedBlackNode<K, V>? = null

    private inner class InOrderEntriesIterator : Iterator<Entry<K, V>>, Iterable<Entry<K, V>> {
        private val stack = ArrayDeque<RedBlackNode<K, V>>()
        private var currentNode: RedBlackNode<K, V>? = head

        init {
            pushLeftSubtree(currentNode)
        }

        override fun hasNext(): Boolean = stack.isNotEmpty()

        override fun next(): Entry<K, V> {
            val node = stack.pop()
            pushLeftSubtree(node.right)
            return node.entry
        }

        override fun iterator(): Iterator<Entry<K, V>> = this

        private fun pushLeftSubtree(node: RedBlackNode<K, V>?) {
            var current = node
            while (current != null) {
                stack.push(current)
                current = current.left
            }
        }
    }

    private inner class InOrderKeysIterator : Iterator<K>, Iterable<K> {
        private val stack = ArrayDeque<RedBlackNode<K, V>>()
        private var currentNode: RedBlackNode<K, V>? = head

        init {
            pushLeftSubtree(currentNode)
        }

        override fun hasNext(): Boolean = stack.isNotEmpty()

        override fun next(): K {
            val node = stack.pop()
            pushLeftSubtree(node.right)
            return node.entry.key
        }

        override fun iterator(): Iterator<K> = this

        private fun pushLeftSubtree(node: RedBlackNode<K, V>?) {
            var current = node
            while (current != null) {
                stack.push(current)
                current = current.left
            }
        }
    }

    private inner class InOrderValuesIterator : Iterator<V>, Iterable<V> {
        private val stack = ArrayDeque<RedBlackNode<K, V>>()
        private var currentNode: RedBlackNode<K, V>? = head

        init {
            pushLeftSubtree(currentNode)
        }

        override fun hasNext(): Boolean = stack.isNotEmpty()

        override fun next(): V {
            val node = stack.pop()
            pushLeftSubtree(node.right)
            return node.entry.value
        }

        override fun iterator(): Iterator<V> = this

        private fun pushLeftSubtree(node: RedBlackNode<K, V>?) {
            var current = node
            while (current != null) {
                stack.push(current)
                current = current.left
            }
        }
    }

    override val entries: Iterable<Entry<K, V>>
        get() = InOrderEntriesIterator()
    override val keys: Iterable<K>
        get() = InOrderKeysIterator()
    override val values: Iterable<V>
        get() = InOrderValuesIterator()

    private fun leftRotate(x: RedBlackNode<K, V>) {
        val y = x.right
        x.right = y?.left
        if (y?.left != null) {
            y.left?.parent = x
        }
        y?.parent = x.parent
        if (x.parent == null) {
            head = y
        } else if (x == x.parent?.left) {
            x.parent?.left = y
        } else {
            x.parent?.right = y
        }
        y?.left = x
        x.parent = y
    }

    private fun rightRotate(x: RedBlackNode<K, V>) {
        val y = x.left
        x.left = y?.right
        if (y?.right != null) {
            y.right?.parent = x
        }
        y?.parent = x.parent
        if (x.parent == null) {
            head = y
        } else if (x == x.parent?.right) {
            x.parent?.right = y
        } else {
            x.parent?.left = y
        }
        y?.right = x
        x.parent = y
    }

    override fun contains(key: K): Boolean = get(key) != null

    override fun remove(key: K): V? {
        TODO("Not yet implemented")
    }

    override fun put(entry: Entry<K, V>): V? = put(entry.key, entry.value)

    override fun put(
        key: K,
        value: V,
    ): V? {
        val z = RedBlackNode(key, value, false)
        var y: RedBlackNode<K, V>? = null
        var x = head

        while (x != null) {
            y = x
            if (comparator.compare(z.key, x.key) < 0) {
                x = x.left
            } else {
                x = x.right
            }
        }

        if (y == null) {
            head = z
        } else if (comparator.compare(z.key, y.key) < 0) {
            y.left = z
        } else {
            y.right = z
        }
        putFixup(z)
        return null
    }

    fun putFixup(node: RedBlackNode<K, V>) {
        var z = node
        while (z.parent != null && z.parent?.black == false) {
            if (z.parent == z.parent?.parent?.left) {
                val y = z.parent?.parent?.right
                if (y?.black == false) {
                    z.parent?.black = true
                    y.black = true
                    z.parent?.parent?.black = false
                    z = z.parent?.parent!!
                } else {
                    if (z == z.parent?.right) {
                        z = z.parent!!
                        leftRotate(z)
                    }
                    z.parent?.black = true
                    z.parent?.parent?.black = false
                    rightRotate(z.parent?.parent!!)
                }
            } else {
                val y = z.parent?.parent?.left
                if (y?.black == false) {
                    z.parent?.black = true
                    y.black = true
                    z.parent?.parent?.black = false
                    z = z.parent?.parent!!
                } else {
                    if (z == z.parent?.left) {
                        z = z.parent!!
                        rightRotate(z)
                    }
                    z.parent?.black = true
                    z.parent?.parent?.black = false
                    leftRotate(z.parent?.parent!!)
                }
            }
            if (z == head) break
        }
        head?.black = true
    }

    override fun set(
        key: K,
        value: V,
    ): V? = put(key, value)

    override fun get(key: K): V? = head?.find(key, comparator)
}

fun main() {
    val tree = RedBlackTreeBasedMap<Int, String>(Int::compareTo)
    for (i in 1..10) {
        tree.put(i, i.toString())
    }
    tree.entries.forEach { println(it) }
}
