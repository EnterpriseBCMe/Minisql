public class BPTree<K extends Comparable<K>, V> { // K:key type; V:value type

    static abstract class Node<K extends Comparable<K>, V> {
        protected NonLeafNode<K, V> parent;
        protected Integer order;
        protected Integer cnt;
        protected K[] keys;

        public Node(Integer order) {
            this.order = order;
            this.cnt = 0;
            this.parent = null;
            //this.keys = new Object[order];
            this.keys = (K[]) new Comparable[order];
        }

        public abstract V find(K key);

        public abstract Node<K, V> insert(K key, V value);

        public boolean isRoot() {
            return this.parent == null;
        }

        public abstract void print(); //Pre-order traversal

        protected int find_index(K key) {
            if (this.cnt == 0) {
                return 0;
            }
            int left = 0, right = this.cnt;
            int index = 0;
            while (left < right) {
                index = (left + right) / 2;
                int cmp = key.compareTo(keys[index]);
                if (cmp < 0) {
                    right = index;
                } else if (cmp > 0) {
                    left = index + 1;
                } else {
                    break;
                }
            }
            if (key.compareTo(this.keys[index]) > 0) {
                index++;
            }
            return index;
        }
    }

    static class NonLeafNode<K extends Comparable<K>, V> extends Node<K, V> {

        protected Node<K, V>[] children;

        public NonLeafNode(Integer order) {
            super(order);
            this.children = new Node[order + 1];
        }

        @Override
        public V find(K key) {
            int i = 0;
            while (i < this.cnt) {
                if (key.compareTo(keys[i]) < 0)
                    break;
                i++;
            }
            if (this.cnt == i && this.children[this.cnt] == null)
                return null;
            return children[i].find(key);
        }

        private NonLeafNode<K, V> insert_and_split(K newKey, Node<K, V> newChild, int index) {
            NonLeafNode<K, V> newNode = new NonLeafNode<>(this.order);
            int pos;
            if (index < this.order / 2) {
                pos = this.order / 2;
                this.keys[pos - 1] = null;
                for (int i = pos; i < this.order; i++) {
                    newNode.keys[i - this.order / 2] = this.keys[i];
                    this.children[i].parent = newNode;
                    newNode.children[i - this.order / 2] = this.children[i];
                    this.keys[i] = null;
                    this.children[i] = null;
                }
                this.children[this.order].parent = newNode;
                newNode.children[this.order - this.order / 2] = this.children[this.order];
                this.children[this.order] = null;
                this.cnt = this.order / 2 - 1;
                newNode.cnt = this.order - this.order / 2;
                this.insert_into_array(newKey, newChild, index);
            } else if (index == this.order / 2) {
                pos = this.order / 2;
                for (int i = pos; i < this.order; i++) {
                    newNode.keys[i - this.order / 2] = this.keys[i];
                    this.children[i + 1].parent = newNode;
                    newNode.children[i - this.order / 2 + 1] = this.children[i + 1];
                    this.keys[i] = null;
                    this.children[i + 1] = null;
                }
                newChild.parent = newNode;
                newNode.children[0] = newChild;
                this.cnt = this.order / 2;
                newNode.cnt = this.order - this.order / 2;
            } else {
                pos = this.order / 2 + 1;
                this.keys[pos - 1] = null;
                for (int i = pos; i < this.order; i++) {
                    newNode.keys[i - pos] = this.keys[i];
                    this.children[i].parent = newNode;
                    newNode.children[i - pos] = this.children[i];
                    this.keys[i] = null;
                    this.children[i] = null;
                }
                this.children[this.order].parent = newNode;
                newNode.children[this.order - pos] = this.children[this.order];
                this.children[this.order] = null;
                this.cnt = this.order / 2;
                newNode.cnt = this.order - pos;
                newNode.insert_into_array(newKey, newChild, index - pos);
            }
            return newNode;
        }

        @Override
        public Node<K, V> insert(K key, V value) {
            int i;
            for (i = 0; i < this.cnt; i++) {
                if (key.compareTo(this.keys[i]) < 0) {
                    break;
                }
            }
            return this.children[i].insert(key, value);
        }

        private void insert_into_array(K key, Node<K, V> node, int index) { //new element will be in [index]
            for (int i = this.cnt; i > index; i--) { //move all elements after index forward
                this.keys[i] = this.keys[i - 1];
                this.children[i + 1] = this.children[i];
            }
            this.keys[index] = key; //insert new key and value
            this.children[index + 1] = node;
            node.parent = this;
            this.cnt++;
        }

        public Node<K, V> insertNode(Node<K, V> node, K key) {
            int index = find_index(key);
            if (this.cnt < this.order) {
                this.insert_into_array(key, node, index);
                return null;
            }
            K oldKey;
            if (index < this.order / 2) {
                oldKey = this.keys[this.order / 2 - 1];
            } else if (index == this.order / 2) {
                oldKey = key;
            } else {
                oldKey = this.keys[this.order / 2];
            }
            NonLeafNode<K, V> newNode = this.insert_and_split(key, node, index);
            if (this.parent == null) {
                NonLeafNode<K, V> newParent = new NonLeafNode<>(this.order);
                newParent.keys[0] = oldKey;
                newParent.cnt = 1;
                this.parent = newParent;
                newParent.children[0] = this;
                newNode.parent = newParent;
                newParent.children[1] = newNode;
                return newParent;
            }
            return this.parent.insertNode(newNode, oldKey);
        }

        @Override
        public void print() {
            for (int i = 0; i < this.cnt; i++) {
                System.out.println(this.keys[i] + " ");
            }
            System.out.println("\n");
            for (int i = 0; i <= this.cnt; i++) {
                this.children[i].print();
            }
        }
    }

    static class LeafNode<K extends Comparable<K>, V> extends Node<K, V> {

        private V[] values;
        private LeafNode<K, V> next;

        public LeafNode(Integer order) {
            super(order);
            this.values = (V[]) new Comparable[order];
            this.next = null;
        }

        @Override
        public V find(K key) {
            int left = 0, right = this.cnt;
            int index;
            while (left < right) {
                index = (left + right) / 2;
                int cmp = key.compareTo(keys[index]);
                if (cmp < 0) {
                    right = index;
                } else if (cmp > 0) {
                    left = index + 1;
                } else {
                    return values[index];
                }
            }
            return null;
        }


        private LeafNode<K, V> insert_and_split(K newKey, V newValue, int index) {
            LeafNode<K, V> newSibling = new LeafNode<>(this.order);
            int pos;
            if (index <= this.order / 2) {
                pos = this.order / 2;
                for (int i = pos; i < this.order; i++) {
                    newSibling.keys[i - this.order / 2] = this.keys[i];
                    newSibling.values[i - this.order / 2] = this.values[i];
                    this.keys[i] = null;
                    this.values[i] = null;
                }
                this.cnt = this.order / 2;
                newSibling.cnt = this.order - this.order / 2;
                this.insert_into_array(newKey, newValue, index);
            } else {
                pos = this.order / 2 + 1;
                for (int i = pos; i < index; i++) {
                    newSibling.keys[i - this.order / 2 - 1] = this.keys[i];
                    newSibling.values[i - this.order / 2 - 1] = this.values[i];
                    this.keys[i] = null;
                    this.values[i] = null;
                }
                newSibling.keys[index - this.order / 2 - 1] = newKey;
                newSibling.values[index - this.order / 2 - 1] = newValue;
                for (int i = index; i < this.order; i++) {
                    newSibling.keys[i - this.order / 2] = this.keys[i];
                    newSibling.values[i - this.order / 2] = this.values[i];
                    this.keys[i] = null;
                    this.values[i] = null;
                }
                this.cnt = this.order / 2 + 1;
                newSibling.cnt = this.order - this.order / 2;
            }
            LeafNode<K, V> temp = this.next;
            this.next = newSibling;
            newSibling.next = temp;
            return newSibling;
        }

        private void insert_into_array(K key, V value, int index) { //new element will be in [index]
            for (int i = this.cnt; i > index; i--) { //move all elements after index forward
                this.keys[i] = this.keys[i - 1];
                this.values[i] = this.values[i - 1];
            }
            this.keys[index] = key; //insert new key and value
            this.values[index] = value;
            this.cnt++;
        }

        @Override
        public Node<K, V> insert(K key, V value) {
            //Find the index of the key (binary search)
            int index = find_index(key);
            if (index < this.cnt && this.keys[index] == key) { //already in
                this.values[index] = value; //update value
                return null;
            }
            if (this.cnt < this.order) { //needn't split
                this.insert_into_array(key, value, index);
                return null;
            }
            LeafNode<K, V> newSibling = this.insert_and_split(key, value, index);
            if (this.parent == null) {
                NonLeafNode<K, V> newParent = new NonLeafNode<>(this.order);
                newParent.keys[0] = newSibling.keys[0];
                newParent.cnt = 1;
                this.parent = newParent;
                newParent.children[0] = this;
                newSibling.parent = newParent;
                newParent.children[1] = newSibling;
                return newParent;
            }
            return this.parent.insertNode(newSibling, newSibling.keys[0]);
        }

        @Override
        public void print() {
            for (int i = 0; i < this.cnt; i++) {
                System.out.println(this.keys[i] + ":" + this.values[i] + " ");
            }
            System.out.println("\n");
        }
    }

    private Node<K, V> root;

    public BPTree(int order) {
        this.root = new LeafNode<>(order);
    }

    public void insert(K key, V value) {
        Node<K, V> ret = root.insert(key, value);
        if (ret != null) {
            this.root = ret;
        }
    }

    public void print() {
        this.root.print();
    }
}
