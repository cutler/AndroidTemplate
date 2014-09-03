package com.cutler.template.common.manager;

public interface ModelCallback {
	/**
	 * 注意：此方法只能在ui线程中被调用。
	 * @param success
	 * @param args
	 */
	void callback(boolean success, Object... args);
}
