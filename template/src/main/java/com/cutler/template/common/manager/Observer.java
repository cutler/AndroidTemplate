package com.cutler.template.common.manager;

/**
 *
 * @author cutler
 */
public interface Observer<T> {
    /**
     * 当数据被改变时，观察者的此方法会被回调。
     * 注意：此方法会在ui线程中被调用。
     *
     * @param data 新数据。
     */
    public void onDataLoaded(T data);
}