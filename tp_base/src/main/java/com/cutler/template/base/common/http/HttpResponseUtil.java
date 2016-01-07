package com.cutler.template.base.common.http;

import android.content.Context;
import android.widget.Toast;

import com.cutler.template.base.R;
import com.cutler.template.base.Template;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 处理Http请求结果的工具类。
 *
 * @author cutler
 */
class HttpResponseUtil {

    /**
     * 验证服务端返回的json是否合法。
     */
    protected static String validateJsonIsException(Object result) {
        Context ctx = Template.getApplication();
        String errorName = null;
        if (result == null) {
            // 若result的值为null，则视为网络连接有问题。
            errorName = ctx.getResources().getString(R.string.error_network);
        } else if (result instanceof JSONObject) {
            // 如果返回值不是JSONObject对象，那么就视为请求成功，否则会进行下面的判断。
            try {
                JSONObject jsonObj = (JSONObject) result;
                // 如果返回的json中包含了success字段，本次请求的结果就由success字段的值来决定，若没包含则直接视为成功。
                // 若success为true，则就视json为正确的json。
                // 若success为false，则还会尝试读取err字段，获取错误的原因。
                if (!jsonObj.optBoolean("success", true)) {
                    errorName = jsonObj.optString("err", ctx.getResources().getString(R.string.error_unknown));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return errorName;
    }

    /**
     * 此方法会在主线程中被调用。
     */
    protected static void defaultException(String errorName) {
        Toast.makeText(Template.getApplication(), errorName, Toast.LENGTH_SHORT).show();
    }

    /**
     * 如果请求返回了200、400以外的状态码，则为它们构造一个json，以便后续统一处理。
     *
     * @param code
     * @return
     */
    protected static String buildJsonByErrorCode(int code) {
        JSONObject result = new JSONObject();
        try {
            // {"success":false,"err":"500"}
            result.put("success", false);
            result.put("err", String.valueOf(code));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

}
