package com.cutler.template.ui.welcome;

import android.app.Activity;
import android.os.Bundle;

import com.cutler.template.R;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		System.out.println("准备！！！");
	}
}
