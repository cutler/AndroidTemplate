package com.cutler.template.common;

import android.content.Context;

import com.cutler.template.Template;
import com.cutler.template.util.io.SharedPreferencesUtil;


/**
 * 本类保存一些程序运行时所需要的全部变量的值。
 *
 * @author cutler
 */
public class InnerSystemParams {

    /**
     * 更新当前用户的位置信息。
     *
     * @param longitude 精度
     * @param latitude  纬度
     */
    public static void setLocation(double longitude, double latitude) {
        SharedPreferencesUtil.putParams(Template.getApplication(), InnerConfig.KEY_LONGITUDE, String.valueOf(longitude));
        SharedPreferencesUtil.putParams(Template.getApplication(), InnerConfig.KEY_LATITUDE, String.valueOf(latitude));
    }

    /**
     * 返回最后一次定位的精度
     *
     * @return
     */
    public static double getLongitude(Context context) {
        return Double.parseDouble(SharedPreferencesUtil.getParams(context, InnerConfig.KEY_LONGITUDE, InnerConfig.INVALID_LOCATION));
    }

    /**
     * 返回最后一次定位的纬度
     *
     * @return
     */
    public static double getLatitude(Context context) {
        return Double.parseDouble(SharedPreferencesUtil.getParams(context, InnerConfig.KEY_LATITUDE, InnerConfig.INVALID_LOCATION));
    }

}