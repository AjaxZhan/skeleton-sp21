package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author CagurZhan
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    private Collection<Node>[] buckets;
    private int M;
    private int N;
    private double FACTOR;

    public MyHashMap() {
        this(16,0.75);
    }
    public MyHashMap(int initialSize) {
        this(initialSize,0.75);
    }

    public MyHashMap(int initialSize, double maxLoad) {
        this.M = initialSize;
        this.N = 0;
        this.FACTOR = maxLoad;
        this.buckets = createTable(initialSize);
        for(int i=0;i<initialSize;i++){
            buckets[i] = createBucket();
        }
    }

    /** 工厂方法*/
    private Node createNode(K key, V value) {
        return new Node(key,value);
    }

    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    private Collection<Node>[] createTable(int tableSize) {
        // 注意这里不能用泛型，Java语法规定
        // 父类数组才能装子类，不用返回LinkedList
        return new Collection[tableSize];
    }

    @Override
    public void clear() {
        this.buckets = null;
        this.N = 0;
        this.FACTOR = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return getNode(key)!=null;
    }

    @Override
    public V get(K key) {
        Node node = getNode(key);
        return node == null ? null : node.value;
    }

    /** 方便后面put改值 */
    private Node getNode(K key){
        if(size() == 0){
            return null;
        }
        int pos = hash(key);
        for (Node node : buckets[pos]) {
            if(node.key.equals(key)){
                return node;
            }
        }
        return null;
    }

    /** Return the pos in the buckets. */
    private int hash(K key){
        return  Math.floorMod(key.hashCode(), M);
    }

    @Override
    public int size() {
        return N;
    }

    @Override
    public void put(K key, V value) {
        Node node = getNode(key);
        if(node!=null){
            node.value = value;
            return ;
        }
        int pos = hash(key);
        buckets[pos].add(createNode(key,value));
        N += 1;
        // 判断是否需要resize
        if((double) N /M >FACTOR){
            resize(2*M);
        }
    }

    /** Resize the buckets Array */
    private void resize(int newSize){
        // 初始化
        Collection<Node>[] table = createTable(newSize);
        for (int i = 0; i < table.length; i++) {
            table[i] = createBucket();
        }
        this.M = newSize;

        for (int i = 0; i < this.buckets.length; i++) {
            for (Node node : this.buckets[i]) {
                int newPos = hash(node.key);
                table[newPos].add(node);
            }
        }
        this.buckets = table;
    }

    @Override
    public Set<K> keySet() {
        HashSet<K> set = new HashSet<>();
        for (Collection<Node> bucket : buckets) {
            for (Node node : bucket) {
                set.add(node.key);
            }
        }
        return set;
    }

    @Override
    public V remove(K key) {
        int pos = hash(key);
        Node node = getNode(key);
        if(node == null ){
            return null;
        }
        buckets[pos].remove(node);
        return node.value;
        // 本项目不需要向下resize
    }

    @Override
    public V remove(K key, V value) {
        Node node = getNode(key);
        if(node == null || !node.value.equals(value)){
            return null;
        }
        return remove(key);
    }

    @Override
    public Iterator<K> iterator() {
        return new MapIterator();
    }

    private class MapIterator implements Iterator{
        Queue<Node> queue;
        public MapIterator(){
            queue = new LinkedList<>();
            for (Collection<Node> bucket : buckets) {
                queue.addAll(bucket);
            }
        }

        @Override
        public boolean hasNext() {
            return !queue.isEmpty();
        }

        @Override
        public Node next() {
            return queue.poll();
        }
    }

}
