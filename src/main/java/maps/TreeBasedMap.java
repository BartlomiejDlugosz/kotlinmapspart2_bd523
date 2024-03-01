package maps;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

class TreeMapNode<K, V> {
    private K key;
    private V value ;

    public TreeMapNode(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Nullable
    private TreeMapNode<K, V> left = null;

    @Nullable
    private TreeMapNode<K, V> right = null;
    public TreeMapNode<K, V> parent = null;

    Entry<K, V> getEntry() {
        return new Entry<>(this.key, this.value);
    }

    void setLeft(@Nullable TreeMapNode<K, V> left) {
        this.left = left;
        if (this.left != null) this.left.parent = this;
    }

    void setRight(@Nullable TreeMapNode<K, V> right) {
        this.right = right;
        if (this.right != null) this.right.parent = this;
    }

    @Nullable
    TreeMapNode<K, V> getLeft() {
        return left;
    }

    @Nullable
    TreeMapNode<K, V> getRight() {
        return right;
    }

    K getKey() {
        return key;
    }

    V getValue() {
        return value;
    }

    void setKey(K key){
        this.key = key;
    }

    void setValue(V value){
        this.value = value;
    }

    V add(K key, V value, Comparator<K> comparator) {
        if (comparator.compare(this.key, key) == 0) {
            V previous = getValue();
            setValue(value);
            return previous;
        }
        if (comparator.compare(this.key, key) > 0) {
            if (this.left == null) {
                this.left = new TreeMapNode<>(key, value);
                this.left.parent = this;
            } else {
                return this.left.add(key, value, comparator);
            }
        } else {
            if (this.right == null) {
                this.right = new TreeMapNode<>(key, value);
                this.right.parent = this;
            } else {
                return this.right.add(key, value, comparator);
            }
        }
        return null;
    }

    V find(K key, Comparator<K> comparator) {
        if (comparator.compare(this.key, key) == 0) return this.value;
        if (comparator.compare(this.key, key) > 0) {
            if (this.left == null) return null;
            return this.left.find(key, comparator);
        }
        if (this.right == null) return null;
        return this.right.find(key, comparator);
    }

    ArrayList<TreeMapNode<K, V>> subTrees() {
        ArrayList<TreeMapNode<K, V>> subTrees = new ArrayList<>();
        if (getLeft() != null) subTrees.add(getLeft());
        if (getRight() != null) subTrees.add(getRight());
        return subTrees;
    }

    TreeMapNode<K, V> getLargest() {
        if (this.right == null) return this;
        return this.right.getLargest();
    }

    TreeMapNode<K, V> getSmallest() {
        if (this.left == null) return this;
        return this.left.getSmallest();
    }
}

public class TreeBasedMap<K, V> implements CustomMutableMap<K, V> {
    protected final Comparator<K> comparator;
    public TreeBasedMap(Comparator<K> keyComparator) {
        this.comparator = keyComparator;
    }
    TreeMapNode<K, V> head = null;

    private class EntriesIterator implements Iterator<Entry<K, V>>, Iterable<Entry<K, V>> {
        private final Deque<TreeMapNode<K, V>> stack = new LinkedList<>();

        public EntriesIterator() {
            TreeMapNode<K, V> current = head;
            while (current != null) {
                stack.push(current);
                current = current.getLeft();
            }
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public Entry<K, V> next() {
            TreeMapNode<K, V> node = stack.pop();
            Entry<K, V> result = node.getEntry();

            if (node.getRight() != null) {
                node = node.getRight();
                while (node != null) {
                    stack.push(node);
                    node = node.getLeft();
                }
            }
            return result;
        }

        @NotNull
        @Override
        public Iterator<Entry<K, V>> iterator() {
            return this;
        }
    }

    private class KeysIterator implements Iterator<K>, Iterable<K> {
        private final EntriesIterator entriesIterator;

        public KeysIterator() {
            this.entriesIterator = new EntriesIterator();
        }

        @Override
        public boolean hasNext() {
            return entriesIterator.hasNext();
        }

        @Override
        public K next() {
            return entriesIterator.next().getKey();
        }

        @NotNull
        @Override
        public Iterator<K> iterator() {
            return this;
        }
    }

    private class ValuesIterator implements Iterator<V>, Iterable<V> {
        private final EntriesIterator entriesIterator;

        public ValuesIterator() {
            this.entriesIterator = new EntriesIterator();
        }

        @Override
        public boolean hasNext() {
            return entriesIterator.hasNext();
        }

        @Override
        public V next() {
            return entriesIterator.next().getValue();
        }

        @NotNull
        @Override
        public Iterator<V> iterator() {
            return this;
        }
    }

    @NotNull
    @Override
    public Iterable<Entry<K, V>> getEntries() {
        return new EntriesIterator();
    }

    @NotNull
    @Override
    public Iterable<K> getKeys() {
        return new KeysIterator();
    }

    @NotNull
    @Override
    public Iterable<V> getValues() {
        return new ValuesIterator();
    }

    @Nullable
    @Override
    public V get(K key) {
        if (head == null) return null;
        return head.find(key, comparator);
    }

    @Nullable
    @Override
    public V set(K key, V value) {
        return put(key, value);
    }

    @Nullable
    @Override
    public V put(K key, V value) {
        if (head == null) {
            head = new TreeMapNode<K, V>(key, value);
            return null;
        }
        return this.head.add(key, value, comparator);
    }

    @Nullable
    @Override
    public V put(@NotNull Entry<K, V> entry) {
        return put(entry.getKey(), entry.getValue());
    }

    @Nullable
    @Override
    public V remove(K key) {
        if (head == null) return null;
        TreeMapNode<K, V> current = head;
        while (current != null) {
            if (comparator.compare(current.getKey(), key) == 0) {
                V val = current.getValue();
                ArrayList<TreeMapNode<K, V>> subTrees = current.subTrees();
                if (subTrees.size() < 2) {
                    deleteNode(current);
                } else {
                    // current has 2 subtrees so definitely has left
                    assert current.getLeft() != null;
                    TreeMapNode<K, V> toRemove = current.getLeft().getLargest();
                    deleteNode(toRemove);
                    current.setKey(toRemove.getKey());
                    current.setValue(toRemove.getValue());
                }
                return val;
            }
            current = comparator.compare(current.getKey(), key) > 0 ? current.getLeft() : current.getRight();
        }
        return null;
    }

    void deleteNode(TreeMapNode<K, V> toRemove) {
        ArrayList<TreeMapNode<K, V>> subTrees = toRemove.subTrees();
        TreeMapNode<K, V> toSet = null;
        if (subTrees.size() == 1) toSet = subTrees.get(0);
        if (toRemove == head) {
            head = toSet;
        } else {
            if (toRemove.parent.getLeft() == toRemove) {
                toRemove.parent.setLeft(toSet);
            } else {
                toRemove.parent.setRight(toSet);
            }
        }
    }

    @Override
    public boolean contains(K key) { return get(key) != null; }
}