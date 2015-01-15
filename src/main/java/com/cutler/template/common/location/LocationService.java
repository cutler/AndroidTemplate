package com.cutler.template.common.location;

import android.os.Handler;
import android.os.Message;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.cutler.template.MainApplication;
import com.cutler.template.common.SystemParams;

/**
 * 使用百度4.2版本的定位 
 * 当您选择使用v4.0及之后版本的定位SDK时，需要先申请且配置Key，并在程序相应位置填写您的Key 。
 * 本人的key为：2dfjxbUBVWpTvHKP4A9YviAG 。
 * 
 * 接入百度定位SDk的具体步骤请参阅：http://api.map.baidu.com/lbsapi/cloud/geosdk-android.htm
 * 本类仅仅是对百度定位SDK的封装。
 * @author cutler
 */
public class LocationService {
	// 用户手动发起一次定位时，设置的回调。
	private static Handler mCallback;
	// 定位成功
	public static final int GET_LOCATION_FINISH = 5221;
	// 定位失败
	public static final int GET_LOCATION_LOST = 5222;
	
    private static LocationClient locationClient;
    private static LocationClientOption option;
    private static BDLocationListener defaultLocationListener;
 	
    /**
     * 启动定位。
     */
    public static void start() {
		if (!isStared()) {
    		locationClient = new LocationClient(MainApplication.getInstance());
    		option = new LocationClientOption();
    		option.setLocationMode(LocationMode.Battery_Saving);	//设置定位模式
    		option.setCoorType("bd09ll");		// 返回的定位结果是百度经纬度,默认值gcj02
    		option.setScanSpan(1000 * 60 * 15);	// 15分钟定位一次。
    		option.setIsNeedAddress(true);		// 返回的定位结果包含地址信息
    		locationClient.setLocOption(option);
    		defaultLocationListener = new MyLocationListener();
    		locationClient.registerLocationListener(defaultLocationListener);
    		locationClient.start();
    	}
    }
    
    /**
     * 检测是否已经启动了定位sdk。
     * @return
     */
    public static boolean isStared() {
        if (locationClient == null) {
            return false;
        }
        return locationClient.isStarted();
    }

    /**
	 * 手动发起一次定位操作，获取用户的当前位置。
	 * 注意：你应该在调用LocationService.start()方法2秒之后再调用此方法，否则定位sdk没初始化完毕是无法进行定位的。
	 */
	public static void getLocation(Handler callback) {
		mCallback = callback;
		setOpenGpsOption(true);
		// 获取定位。
		int requestCode =  locationClient.requestLocation();
		/*
			0：正常发起了定位。
			1：服务没有启动。
			2：没有监听函数。
			6：请求间隔过短。 前后两次请求定位时间间隔不能小于1000ms。
		*/
		if(requestCode != 0) {
			onReceiveLocationLost();
		}
	}

	/**
	 * 开启或关闭gps定位。
	 * @param isOpen
	 */
    public static void setOpenGpsOption(boolean isOpen) {
        option.setOpenGps(isOpen);
        locationClient.setLocOption(option);
    }
    
	/*
	 * 定位失败
	 */
	private static void onReceiveLocationLost() {
		if (mCallback != null) {
			mCallback.sendEmptyMessage(GET_LOCATION_LOST);
			mCallback = null;
		}
	}

    final static class MyLocationListener implements BDLocationListener {
        public void onReceiveLocation(BDLocation location) {
        	// 关闭GPS
        	if(option.isOpenGps()){
        		setOpenGpsOption(false);
        	}
            if (location == null) {
            	onReceiveLocationLost();
                return;
            }
            Double longitude = Double.valueOf(location.getLongitude());
			Double latitude = Double.valueOf(location.getLatitude());
            if(Double.valueOf(4.94065645841247e-324).equals(longitude)|| Double.valueOf(4.94065645841247e-324).equals(latitude)) {
            	onReceiveLocationLost();
				return ;
			}
            // 若用户需要定位信息。
 			if(mCallback != null){
 				Message msg = mCallback.obtainMessage(GET_LOCATION_FINISH);
 				msg.obj = location;
 				mCallback.sendMessage(msg);
 				mCallback = null;
 			}
 			// 将定位信息保存到本地。
			SystemParams.setLocation(location.getLongitude(), location.getLatitude());
        }
    }
    
    /**
     * 停止定位。
     */
    public static void stop() {
        if (locationClient != null) {
            locationClient.stop();
            locationClient.unRegisterLocationListener(defaultLocationListener);
        }
    }
}