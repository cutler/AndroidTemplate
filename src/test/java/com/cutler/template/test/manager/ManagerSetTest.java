package com.cutler.template.test.manager;

import android.test.AndroidTestCase;

import com.cutler.template.common.manager.Observer;
import com.cutler.template.test.manager.model.geocoding.Geocoding;
import com.cutler.template.test.manager.model.geocoding.GeocodingEntityManagerSet;

public class ManagerSetTest extends AndroidTestCase {

	Observer<Geocoding> ob1 = new Observer<Geocoding>() {
		public void onDataLoaded(Geocoding data) {
			System.out.println("观察者(ob1)接到通知："+data);
		}
	};
	Observer<Geocoding> ob2 = new Observer<Geocoding>() {
		public void onDataLoaded(Geocoding data) {
			System.out.println("观察者(ob2)接到通知："+data);
		}
	};
	
	/**
	 * 获取纬度信息
	 * @throws InterruptedException 
	 */
	public void testGetGeocoding() throws InterruptedException{
		GeocodingEntityManagerSet.getInstance().addObserver("北京市", ob1, null, null);
		GeocodingEntityManagerSet.getInstance().addObserver("北京市", ob2, null, null);
		Thread.sleep(10 * 1000);
	}
	
	public void testGetGeocoding2() throws InterruptedException{
		GeocodingEntityManagerSet.getInstance().addObserver("北京市", ob1, null, null);
		GeocodingEntityManagerSet.getInstance().addObserver("北京市", ob2, null, null);
		GeocodingEntityManagerSet.getInstance().addObserver("上海市", ob2, null, null);
		Thread.sleep(10 * 1000);
	}
	
}
