package bstmap;



import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Cagur
 * @version 1.0
 */
public class BSTMap<K extends Comparable<K>,V> implements Map61B<K ,V>{

    private BSTNode root;

    private class BSTNode{
        private K key;
        private V val;
        private BSTNode left,right;
        private int size;

        public BSTNode(K key, V val, int size){
            this.key = key;
            this.val = val;
            this.size = size;
        }
    }

    public BSTMap() {

    }

    public void printInOrder(){
        printInOrder(root);
    }

    /** Print the BST in order of increasing Key. */
    private void printInOrder(BSTNode node){
        // 本质上是做一个中序遍历
        if(node == null){
            return;
        }
        printInOrder(node.left);
        System.out.println(node.key.toString() + "--" + node.val.toString());
        printInOrder(node.right);
    }

    @Override
    public void clear() {
        root = null;
    }

    @Override
    public boolean containsKey(K key) {
        if(key == null){
            throw new IllegalArgumentException("key should not be null.");
        }
//        return get(root,key) != null;
//        不能直接用get，否则会如果直接给你put一个null是过不了的，需要用递归。
        return containsKey(root,key);
    }

    private boolean containsKey(BSTNode node, K key){
        if(node == null){
            return false;
        }
        int cmp = key.compareTo(node.key);
        if(cmp < 0){
            // key < node.key , find left
            return containsKey(node.left,key);
        }else if(cmp >0){
            return containsKey(node.right,key);
        }
        return true;
    }

    @Override
    public V get(K key) {
        return get(root,key);
    }

    private V get(BSTNode node, K key){
        if(key == null){
            throw new IllegalArgumentException("key is null");
        }
        if(node == null){
            return null;
        }
        int cmp = key.compareTo(node.key);
        if(cmp == 0){
            return node.val;
        }else if(cmp < 0){
            // node.key > key , find in left tree
            return get(node.left,key);
        }else{
            return get(node.right,key);
        }
    }

    @Override
    public int size() {
        return size(root);
    }

    private int size(BSTNode node){
        if(node == null){
            return 0;
        }
//        将size的改变放到add中，维护size，提高速度
//        return size(node.left) + size(node.right) + 1;
        return node.size;
    }

    @Override
    public void put(K key, V value) {
        if(key == null) {
            throw new IllegalArgumentException("key should not be null");
        }
//        如果value为空，执行删除操作
        if(containsKey(key) && value == null){
            remove(key);
        }
        root = put(root,key,value);
    }

    private BSTNode put(BSTNode node, K key, V value){
        if(node == null) {
            return new BSTNode(key,value,1);
        }
        int cmp = key.compareTo(node.key);
        if(cmp == 0) {
            node.val = value;
        }else if(cmp < 0){
            // key < node.key , put it left
            node.left =  put(node.left,key,value);
        }else{
            node.right = put(node.right,key,value);
        }
        node.size = 1 + size(node.left) + size(node.right);
        return node;
    }

    @Override
    public Set<K> keySet() {
        if(root == null){
            return null;
        }
        HashSet<K> ks = new HashSet<>();
        addKey(root,ks);
        return ks;
    }
//    辅助函数，递归添加set
    private void addKey(BSTNode node, Set<K>set){
        if(node == null){
            return ;
        }
        // 先序遍历
        set.add(node.key);
        addKey(node.left,set);
        addKey(node.right,set);
    }

//    删除的逻辑:
//      1. 如果只有左子树或者右子树：直接将儿子赋给自己。
//      2. 如果同时有左和右，找到右子树的最小元素赋给自己，删除右子树的最小元素。
//    所以，要实现删除，需要构造【找到最小】和【删除最小】两个辅助函数。

    /** Remove min node in BSTMap */
    private BSTNode removeMin(BSTNode node){
//        找到最小的节点，当最小的时候，将让左儿子指向右孙子
        if(node.left == null){
            return node.right;
        }
        node.left =  removeMin(node.left);
//        注意减小size，实际上，改变指针的指向后，size函数就不会统计对应的大小了。
//        这里主要是更新父节点的size
        node.size = size(node.left) + size(node.right) + 1;
        return node;
    }

    /** Return the smallest node */
    private BSTNode min(BSTNode node){
        if(node.left == null) {
            return node;
        }
        return min(node.left);
    }

    @Override
    public V remove(K key) {
        if(key == null) {
            throw new IllegalArgumentException("key should not be null.");
        }
        if(!containsKey(key)){
            return null;
        }
        V val = get(key);
//      将查找的逻辑放到帮助函数内
        root = remove(root,key);
        return val;
    }

    private BSTNode remove(BSTNode node, K key){
        if(node == null){
            return null;
        }
        int cmp = key.compareTo(node.key);
        if(cmp < 0){
            // key < node.key , find in left
            node.left = remove(node.left,key);
        }else if(cmp > 0){
            node.right = remove(node.right,key);
        }else{
            // 分类讨论
            if(node.right == null) return node.left;
            if(node.left == null) return node.right;
            BSTNode tmp = node;
            node = min(node.right);
            node.right = removeMin(tmp.right);
            node.left = tmp.left;
        }
//        更新size
        node.size = size(node.left) + size(node.right)  + 1;
        return node;
    }

    @Override
    public V remove(K key, V value) {
        if(key == null){
            throw new  IllegalArgumentException("key should not be null.");
        }
        if(!containsKey(key)){
            return null;
        }
        if(get(key)!=value){
            return  null;
        }
        return remove(key);
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }
}
