package com.cutler.template.base.common.manager;

import com.cutler.template.base.Template;

import java.lang.ref.SoftReference;
import java.util.LinkedList;
import java.util.List;

/**
 * Model管理类
 *
 * @author cutler
 */
public class EntityManager<T> {
    // 保存所有注册到本Manager中的观察者。
    private List<SoftReference<Observer<T>>> observers = new LinkedList<SoftReference<Observer<T>>>();
    // 本类所管理的数据。
    private T data;

    /**
     * 添加一个观察者。
     */
    public void addObserver(final Observer<T> observer) {
        if (observer == null) {
            throw new NullPointerException();
        }
        // 同步块，防止并发修改observers对象。
        synchronized (observers) {
            removeObserver(observer);
            observers.add(new SoftReference<Observer<T>>(observer));
        }
    }

    /**
     * 删除一个观察者。
     */
    public void removeObserver(Observer<T> observer) {
        synchronized (observers) {
            observers.remove(getOld(observer));
        }
    }

    // 查看observer是否已存在列表中。
    private synchronized SoftReference<Observer<T>> getOld(Observer<T> observer) {
        SoftReference<Observer<T>> old = null;
        for (SoftReference<Observer<T>> item : observers) {
            if (item.get() != null && item.get() == observer) {
                old = item;
                break;
            }
        }
        return old;
    }

    /**
     * 通知Manager中已注册的所有观察者，数据已经更新了。
     */
    protected void notifyObservers() {
        Template.getMainHandler().post(new Runnable() {
            public void run() {
                synchronized (observers) {
                    for (SoftReference<Observer<T>> item : observers) {
                        if (item.get() != null) {
                            item.get().onDataLoaded(data);
                        }
                    }
                }
            }
        });
    }

    /**
     * 返回当前Manager的观察者个数。
     */
    public int getObserverCount() {
        return observers.size();
    }

    /**
     * @return 返回当前Manager当前管理的数据。
     */
    public T getData() {
        return data;
    }

    /**
     * @return 設置当前Manager当前管理的数据。
     */
    public void setData(T data) {
        this.data = data;
    }
}