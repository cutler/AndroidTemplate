package com.cutler.template.common.manager;

import android.os.Handler;
import android.os.Looper;

import com.cutler.template.Template;

import java.util.ArrayList;
import java.util.List;

/**
 * Model管理类
 *
 * @author cutler
 */
public class EntityManager<T> {
    // 保存所有注册到本Manager中的观察者。
    private List<Observer<T>> observers = new ArrayList<Observer<T>>();
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
            observers.remove(observer);
            observers.add(observer);
        }
    }

    /**
     * 删除一个观察者。
     */
    public void removeObserver(Observer<T> observer) {
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    /**
     * 通知Manager中已注册的所有观察者，数据已经更新了。
     */
    protected void notifyObservers() {
        Template.getMainHandler().post(new Runnable() {
            public void run() {
                // 此处不用担心会阻塞主线程，从以往的经验来看，通常观察者的数量不会导致主线程明显阻塞。
                synchronized (observers) {
                    for (Observer<T> observer : observers) {
                        observer.onDataLoaded(data);
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