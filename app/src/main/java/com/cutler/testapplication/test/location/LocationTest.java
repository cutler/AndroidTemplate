package com.cutler.testapplication.test.location;

import android.os.Handler;
import android.os.Message;
import android.test.AndroidTestCase;

import com.baidu.location.BDLocation;
import com.cutler.template.common.location.LocationService;

public class LocationTest {

	/**
	 * 获取用户的位置信息。
	 * 注意：如果想成功获取位置，需要把这个方法内部的代码copy到Activity中。 把他们 这里只是为了演示怎么调用定位功能。
	 * @throws InterruptedException 
	 */
	public void testGetLocation() {
		// 在调用LocationService.start()之后3秒左右的时间（因为百度SDK需要执行初始化操作），再调用LocationService.getLocation()方法获取位置即可。
		LocationService.getLocation(new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case LocationService.GET_LOCATION_FINISH:
						BDLocation location = (BDLocation) msg.obj;
						System.out.println(location.getAddrStr() + "," + location.getLongitude() + "," + location.getLatitude());
						break;
					case LocationService.GET_LOCATION_LOST:
						System.out.println("定位失败！！！");
						break;
				}
			}
		});
	}
}
