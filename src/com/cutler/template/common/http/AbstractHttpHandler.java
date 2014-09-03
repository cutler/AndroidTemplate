package com.cutler.template.common.http;

/**
 * 为方便使用ServiceHandler，本类特地重写了三个不常用的方法。
 * @author cutler
 *
 */
public abstract class AbstractHttpHandler implements IHttpHandler{

	@Override
	public boolean handleError(String result) {
		return false;
	}
	
	@Override
	public void onAfterHandle(boolean success) {
		
	}
	
	@Override
	public void onBeforeHandle(boolean success) {
		
	}

}
