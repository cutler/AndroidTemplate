package com.cutler.template.test.location;

import com.baidu.location.BDLocation;
import com.cutler.template.common.location.LocationService;

import android.os.Handler;
import android.os.Message;
import android.test.AndroidTestCase;

public class LocationTest extends AndroidTestCase {

	/**
	 * 获取用户的位置信息。
	 * 注意：如果想成功获取位置，需要把这个方法内部的代码copy到Activity中。 把他们 这里只是为了演示怎么调用定位功能。
	 * @throws InterruptedException 
	 */
	public void testGetLocation() throws InterruptedException {
		// 程序启动的时候，调用此方法启动定位服务。
		LocationService.start();
		// 在调用LocationService.start()之后3秒左右的时间（因为百度SDK需要执行初始化操作），再调用LocationService.getLocation()方法获取位置即可。
		new Handler().postDelayed(new Runnable() {
			public void run() {
				LocationService.getLocation(new Handler(){
					public void handleMessage(Message msg) {
						switch (msg.what) {
						case LocationService.GET_LOCATION_FINISH:
							BDLocation location = (BDLocation) msg.obj;
							System.out.println(location.getAddrStr()+","+location.getLongitude()+","+location.getLatitude());
							break;
						case LocationService.GET_LOCATION_LOST:
							System.out.println("定位失败！！！");
							break;
						}
					}
				});
			}
		}, 3000);
		Thread.sleep(10000);
	}
}
