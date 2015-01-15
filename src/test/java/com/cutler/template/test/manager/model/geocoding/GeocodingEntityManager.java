package com.cutler.template.test.manager.model.geocoding;

import java.util.HashMap;

import org.json.JSONObject;

import com.cutler.template.common.http.HttpCaller;
import com.cutler.template.common.http.HttpHandler;
import com.cutler.template.common.manager.EntityManagerSet;
import com.cutler.template.common.manager.InSetEntityManager;
import com.cutler.template.common.manager.ModelCallback;

/**
 * 本类用来管理一个Weather对象。
 * @author cutler
 *
 */
public class GeocodingEntityManager extends InSetEntityManager<String, Geocoding>{

	protected GeocodingEntityManager(
			EntityManagerSet<String, ? extends InSetEntityManager<String, Geocoding>> managerSet,
		String key) {
		super(managerSet, key);
	}

	@Override
	protected void fetchData(final ModelCallback callback) {
		HashMap<String,String> params = new HashMap<String,String>();
		params.put("a", getKey());
		HttpCaller.getInstance().service(HttpCaller.base_url+"geocoding", params, new HttpHandler() {
			public void handleResult(Object result) {
				System.out.println("获取信息成功");
				callback.callback(true, Geocoding.parseJSON((JSONObject) result));
			}
		});
	}

}
