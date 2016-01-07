package com.cutler.template.base.common.http;

/**
 * 发送Http请求时所用到的回调接口，具体参看HttpTask类。
 *
 * @author cutler
 */
public abstract class HttpHandler {

    /**
     * 请求成功时调用。
     * 注意：此方法会在ui线程中被调用。
     */
    public abstract void handleResult(Object result);

    /**
     * 请求没有成功时调用。
     * 注意：此方法会在ui线程中被调用。
     */
    public boolean handleError(String result) {
        return false;
    }

    /**
     * 不论请求是否成功，都会在handleResult和handleError方法之前被调用。
     * 注意：此方法会在ui线程中被调用。
     */
    public void onBeforeHandle(boolean success) {

    }

    /**
     * 不论请求是否成功，都会在handleResult和handleError方法之后被调用。
     * 注意：此方法会在ui线程中被调用。
     */
    public void onAfterHandle(boolean success) {

    }

    /**
     * 当请求被取消时调用此方法。
     * 注意：此方法会在ui线程中被调用。
     */
    public void onCancelled() {

    }
}
