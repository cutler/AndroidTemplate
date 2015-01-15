package com.cutler.template.test.manager.model.geocoding;

import android.app.Activity;

import com.cutler.template.common.manager.EntityManagerSet;
import com.cutler.template.common.manager.Observer;

public class GeocodingEntityManagerSet extends EntityManagerSet<String, GeocodingEntityManager>{

	private GeocodingEntityManagerSet() {
		// 内存中最多同时保存3个WeatherEntityManager对象。
		super(3);
	}
	
	@Override
	protected GeocodingEntityManager createEntityManager(String key) {
		return new GeocodingEntityManager(this, key);
	}

	// 单例对象。
	private static GeocodingEntityManagerSet instance;

	/**
	 * @return 单例对象。
	 */
	public static GeocodingEntityManagerSet getInstance() {
		if (instance == null) {
			synchronized (GeocodingEntityManagerSet.class) {
				if (instance == null) {
					instance = new GeocodingEntityManagerSet();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 添加一个观察者。
	 * 注意：此方法只能在ui线程中被调用。
	 * @param key 用户的id。
	 */
	public void addObserver(String key, Observer<Geocoding> observer, Activity act, String msg){
		GeocodingEntityManager mgr = get(key);
		if(mgr != null){
			mgr.addObserver(observer, act, msg);
		}
	}
	
	/**
	 * 删除一个观察者。
	 * @param key 用户的id。
	 */
	public void removeObserver(String key, Observer<Geocoding> observer){
		GeocodingEntityManager mgr = get(key);
		if(mgr != null){
			mgr.removeObserver(observer);
		}
	}
}
