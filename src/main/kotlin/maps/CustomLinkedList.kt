package maps

interface Node<T> {
    var next: Node<T>?
}

class RootNode<T> : Node<T> {
    override var next: Node<T>? = null
}

class ValueNode<T>(val value: T) : Node<T> {
    override var next: Node<T>? = null
}

open class CustomLinkedList<T> : MutableIterable<T> {
    private var root: Node<T> = RootNode()

    val isEmpty: Boolean
        get() = root.next == null

    val head: T?
        get() = (root.next as? ValueNode<T>)?.value

    open fun add(value: T) {
        val newNode = ValueNode(value)
        newNode.next = root.next
        root.next = newNode
    }

    fun remove(): T? {
        val headNode = root.next as? ValueNode<T>
        root.next = headNode?.next
        return headNode?.value
    }

    override fun iterator(): MutableIterator<T> {
        return object : MutableIterator<T> {
            var current: Node<T>? = root
            var nextItem: Node<T>? = root.next
            var lastItem: Node<T>? = null
            var removedLast = false

            override fun hasNext(): Boolean = nextItem != null

            override fun next(): T {
                if (!hasNext()) throw NoSuchElementException()

                // Move everything forward ONE step
                lastItem = current
                current = nextItem
                nextItem = current?.next

                removedLast = false
                return (current as? ValueNode<T>)?.value ?: throw NoSuchElementException()
            }

            override fun remove() {
                if (lastItem == null) throw UnsupportedOperationException()
                lastItem?.next = nextItem

                // Reset state for consistency
                current = lastItem
                nextItem = lastItem?.next

                removedLast = true
            }
        }
    }
}

fun main() {
    val cll = CustomLinkedList<List<Double>>()
    cll.add(listOf(4.0))
    cll.add(listOf(9.9, 1.1))
    cll.add(listOf(3.2))
    val cllIterator = cll.iterator()
    while (cllIterator.hasNext()) {
        cllIterator.next()
        cllIterator.remove()
    }
    println(cll)
}
