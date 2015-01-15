package com.cutler.template.test.manager.model.geocoding;

import org.json.JSONObject;

public class Geocoding {

	private String info;

	public String getInfo() {
		return info;
	}

	public static Geocoding parseJSON(JSONObject jsonObj) {
		Geocoding inst = new Geocoding();
		try {
			inst.info = jsonObj.toString();
		} catch (Exception e) {
			inst = null;
			e.printStackTrace();
		}
		return inst;
	}

	@Override
	public String toString() {
		return "Geocoding [info=" + info + "]";
	}

}
