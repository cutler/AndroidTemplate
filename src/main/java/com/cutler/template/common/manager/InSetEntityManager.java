package com.cutler.template.common.manager;


/**
 * 本类的子类不应该是单例模式的。
 * @author cutler
 */
public abstract class InSetEntityManager<K, V> extends EntityManager<V> {
	// 保留一个EntityManagerSet的引用，主要用于调用该类的evictStickyItem方法。
	private EntityManagerSet<K, ? extends InSetEntityManager<K, V>> managerSet;
	private K key;

	protected InSetEntityManager(
			EntityManagerSet<K, ? extends InSetEntityManager<K, V>> managerSet, K key) {
		// 为managerSet和key初始化
		this.key = key;
		this.managerSet = managerSet;
	}

	/**
	 * 注意：此方法只能在ui线程中被调用。
	 */
	public synchronized void removeObserver(Observer<V> observer) {
		super.removeObserver(observer);
		// 判断如果不再剩余任何observer，调用managerSet.evictStickyItem(key)
		if(getObserverCount() == 0 && managerSet != null){
			// 当一个InSetEntityManager没有任何观察者时，它可能处于两种情况：
			// 1. 已经从Lru缓存中移除，并被保存在stickyEntries中。
			// 2. 未从Lru缓存中移除。                 
			// 不论哪一种情况，都调用一下此方法。
			managerSet.evictStickyItem(key);
		}
	}

	public K getKey() {
		return key;
	}
	
}
