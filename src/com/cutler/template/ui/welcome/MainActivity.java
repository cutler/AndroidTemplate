package com.cutler.template.ui.welcome;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.baidu.location.BDLocation;
import com.cutler.template.R;
import com.cutler.template.common.location.LocationService;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 开启定位服务。
        LocationService.start();
        new Handler().postDelayed(new Runnable() {
			public void run() {
				System.out.println("开始定222位 "+
				LocationService.getLocation(new Handler(){
		        	public void handleMessage(Message msg) {
		        		System.out.println(((BDLocation)msg.obj).getAddrStr());
		        	}
		        }));;				
			}
		}, 3000);
	}
}
