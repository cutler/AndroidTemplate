package com.cutler.template;

import android.app.Application;

public class MainApplication extends Application {

	private static MainApplication instance;

	@Override
	public void onCreate() {
		instance = this;
		super.onCreate();
	}

	public static MainApplication getInstance() {
		return instance;
	}

}
