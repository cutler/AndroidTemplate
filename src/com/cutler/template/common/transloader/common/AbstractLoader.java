package com.cutler.template.common.transloader.common;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.cutler.template.common.transloader.goal.TransloadObserver;

public abstract class AbstractLoader {

	// 保存所有注册到本loader中的观察者。
	protected List<TransloadObserver> observers;

	public AbstractLoader() {
		observers = new CopyOnWriteArrayList<TransloadObserver>();
	}
	
	/**
	 * 添加观察者。
	 * @param observer
	 */
	public void addObserver(TransloadObserver observer){
		if (observer != null) {
			observers.add(observer);
		}
	}
	
	/**
	 * 删除观察者。
	 * @param observer
	 */
	public void removeObserver(TransloadObserver observer){
		if (observer != null) {
			observers.remove(observer);
		}
	}
	
	
    /**
     * 在主线程中，通知所有观察者数据已经改变。 
     */
    public void notifyObservers(final Map<String,Object> params){
//		mHandler.post(new Runnable() {
//			public void run() {
//				if(observers != null){
//					// 通知所有观察者，数据已经改变。
//	            	Iterator<DownloadObserver> iter = observers.iterator();
//	            	while(iter.hasNext()){
//	            		iter.next().onDownloadDataChanged(params);
//	            	}
//				}
//			}
//		});
    }
}
