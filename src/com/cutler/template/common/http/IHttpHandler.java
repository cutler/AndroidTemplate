package com.cutler.template.common.http;

/**
 * 发送Http请求时所用到的回调接口，具体参看Caller.service()方法
 * @author cutler
 *
 */
public interface IHttpHandler {
	/**
	 * 注意：此方法只能在ui线程中被调用。
	 */
	void handleResult(Object result);

	/**
	 * 注意：此方法只能在ui线程中被调用。
	 */
	boolean handleError(String result);

	/**
	 * 注意：此方法只能在ui线程中被调用。
	 */
	void onBeforeHandle(boolean success);

	/**
	 * 注意：此方法只能在ui线程中被调用。
	 */
	void onAfterHandle(boolean success);
}
