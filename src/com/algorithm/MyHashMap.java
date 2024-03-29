package com.algorithm;
//Use generic type
//hash map is implmented by a array of linkedlist
//This hash map supports concurrency
public class MyHashMap<K, V> {
	
	public static class Node<K, V> {
		private final K key;
		private V value;
		private Node<K, V> next;
		public Node(K key, V value) {
			this.key = key;
			this.value = value;
			this.next = null;
		}
		public K getKey() {//for concurrency, we do not care about those methods
			return this.key;
		}
		public V getValue() {
			return this.value;
		}
		public void setValue(V value) {
			this.value = value;
		}
	}
	//define the default capacity of hashmap is 16.
	public static final int DEFAULT_INITIAL_CAPACITY = 16;
	//if the load factor > 0.75, then resize the hashmap and rehashing
	public static final float DEFAULT_LOAD_FACTOR = 0.75f;
	private Node<K, V>[] buckets;
	private int size;
	private float loadFactor;
	//default consturctor
	public MyHashMap() {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
	}
	//customized constructor
	public MyHashMap(int cap, float loadFactor) {
		if (cap < 0) {
			throw new IllegalArgumentException("Capacity cannot be < 0");
		}
		this.size = 0;
		this.buckets = (Node<K, V>[])(new Node[cap]);
		this.loadFactor = loadFactor;
	}
	
	public synchronized int size() {//sync
		return size;
	}
	public synchronized boolean isEmpty() {//sync
		return size == 0;
	}
	/* Put: 1. find the target index in array
	 * 	2. check whether the node is in the linkedlist
	 * 		2.1. if exist, update the value
	 *		2.2. if not, insert a new node to head.
	 */
	public synchronized V put(K key, V value) {//sync
		int index = getIndex(key);
		Node<K, V> head = buckets[index];
		Node<K, V> node = head;
		while (node != null) {
			if (equalsKey(node.key, key)) {
				V oldValue = node.value;
				node.value = value;
				return oldValue;
			}
			node = node.next;
		}
		Node<K, V> newNode = new Node<K, V>(key, value);
		newNode.next = head;
		buckets[index] = newNode;//new head is here.
		size++;
		if (needRehashing()) {
			rehashing();
		}
		return null;
	} 
	public synchronized V get(K key) {
		int index = getIndex(key);
		Node<K, V> head = buckets[index];
		while (head != null) {
			if (equalsKey(head.key, key)) {
				return head.value;
			}
			head = head.next;
		}
		return null;
	}
	/* In Java, there is a contract between equals() and hashCode(),
	 * the developers need to maintain:
	 * 	1. If one.equals(two), it is a must that one.hashCode() == two.hashCode()
	 *	2. If one.hashCode() == two.hashCode(), it is not necessary one.equals(two)
	 *  When you want to override equals(), please definitely override hashCode() as well :)
	 */
	public boolean containsKey(K key) {
		int index = getIndex(key);
		Node<K, V> head = buckets[index];
		while (head != null) {
			if (equalsKey(head.key, key)) {
				return true;
			}
			head = head.next;
		}
		return false;
	}
	public synchronized V remove(K key) {//sync
		int index = getIndex(key);
		Node<K, V> head = buckets[index];
		Node<K, V> dummy = new Node<K, V>(head.key, head.value);
		dummy.next = head;
		Node<K, V> cur = dummy;
		while (head != null) {
			if (equalsKey(head.key, key)) {
				V oldValue = head.value;
				cur.next = head.next;
				head.next = null;
				buckets[index] = dummy.next;
				size--;
				return oldValue;
			}
			head = head.next;
			cur = cur.next;
		}
		return null;
	}
	public boolean containsValue(V value) {
		if (isEmpty()) {
			return false;
		}
		for (Node<K, V> cur : buckets) {
			while (cur != null) {
				if (equalsValue(cur.value, value)) {
					return true;
				}
				cur = cur.next;
			}
		}
		return false;
	}
	private boolean equalsKey(K k1, K k2) {
		if (k1 == null && k2 == null) {
			return true;
		} else if (k1 == null || k2 == null) {
			return false;
		}
		return k1.equals(k2);
	}
	
	private boolean equalsValue(V v1, V v2) {
		if (v1 == null && v2 == null) {
			return true;
		} else if (v1 == null || v2 == null) {
			return false;
		}
		return v1.equals(v2);
	}
//  Those private methods are within the synchronized methods, so we don't need to sync them.
	private int hash(K key) {
		if (key == null) {
			return 0;
		}
		//make sure the hashCode is positive number
		return key.hashCode() & 0X7FFFFFFF;
	}
	
	private int getIndex(K key) {
		return hash(key) % buckets.length;
	}
	
	private boolean needRehashing() {
		float ratio = (size + 0.0f) / buckets.length;
		return ratio > loadFactor;
	}
	
	private void rehashing() {
		Node<K, V>[] array = (Node<K, V>[])(new Node[2*buckets.length]);
		for (int i = 0; i < buckets.length; i++) {
			array[i] = buckets[i];
		}
		buckets = array;
	}
	
// 	public static void main(String[] args) {
// 		// TODO Auto-generated method stub
// 		MyHashMap<String, Integer> sol = new MyHashMap<>();
// 		System.out.println(sol.isEmpty());
// 		System.out.println(sol.size());
// 		System.out.println(sol.put("Tom", 5));
// 		System.out.println(sol.put("Jerry", 10));
// 		System.out.println(sol.get("Tom"));
// 		System.out.println(sol.put("Tom", 10));
// 		System.out.println(sol.size());
// 		System.out.println(sol.remove("Tom"));
// 	}

}
