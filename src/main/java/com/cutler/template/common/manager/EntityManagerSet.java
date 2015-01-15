package com.cutler.template.common.manager;

import java.util.HashMap;
import java.util.Map;

import android.support.v4.util.LruCache;

/**
 * 每个EntityManager类都用来管理一个特定的数据，当数据改变时则会依次通知其自己所有的观察者。
 * 本类使用Lru缓存的方式，来管理一组EntityManager。
 * 每个被管理的EntityManager都必须是InSetEntityManager的子类。
 * @author cutler
 */
public abstract class EntityManagerSet<K, V extends InSetEntityManager<K, ?>> extends LruCache<K, V> {
	
	// 由于Lru的特点，会导致从Lru缓存中移除的InSetEntityManager实例，仍然拥有自己的观察者，因此使用了HashMap来缓存那些从Lru缓存中被移除，但是仍有有观察者的InSetEntityManager。
	private Map<K, V> stickyEntries = new HashMap<K, V>();

	protected EntityManagerSet(int maxSize) {
		super(maxSize);
	}

	// 重写基类LruCache的方法，当某个元素被从LruCache中移除时，会回调此方法。
	protected synchronized void entryRemoved(boolean evicted, K key,
			V oldValue, V newValue) {
		// 判断如果oldValue的observer列表不为空，将其放入stickyEntries中。
		if(oldValue != null && oldValue.getObserverCount() > 0){
			stickyEntries.put(key, oldValue);
		}
	}

	// 重写基类的方法。
	// 当用户试图调用LruCache的get(key)方法获取Value，且LruCache中并没有保存该key时，调用此方法。
	protected synchronized V create(K key) {
		V retVal;
		// 判断如果key在stickyEntries中，直接返回stickyEntries.get(key).
		if(stickyEntries.containsKey(key)){
			retVal = stickyEntries.remove(key);
		} else {	// 否则返回createEntityManager(key)
			retVal = createEntityManager(key);
		}
		return retVal;
	}

	/**
	 * 外界也可以手动的将InSetEntityManager从stickyEntries中移除。
	 */
	public synchronized void evictStickyItem(K key) {
		// 从stickyEntries中删去key
		stickyEntries.remove(key);
	}
	
	public synchronized void evictStickyAll(){
		stickyEntries.clear();
	}

	abstract protected V createEntityManager(K key);
	
}
