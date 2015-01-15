package com.cutler.template.common.manager;

/**
 * @author cutler
 */
public interface ModelCallback {
	/**
	 * 注意：此方法只能在ui线程中被调用。
	 */
	void callback(boolean success, Object... args);
}
