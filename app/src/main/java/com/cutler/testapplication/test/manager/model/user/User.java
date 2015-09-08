package com.cutler.testapplication.test.manager.model.user;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private int id;
    private String name;
    private String signature;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * 将一个json字符串转化为一个User对象。
     *
     * @param jsonObj
     * @return
     */
    public static User parseJSON(JSONObject jsonObj) {
        User inst = new User();
        try {
            inst.id = jsonObj.getInt("id");
            inst.name = jsonObj.getString("name");
            inst.signature = jsonObj.getString("signature");
        } catch (JSONException e) {
            inst = null;
        }
        return inst;
    }

    /**
     * 将User对象转换成一个JSON字符串。
     *
     * @return
     */
    public String toJSON() {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("id", id);
            jsonObj.put("name", name);
            jsonObj.put("signature", signature);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj.toString();
    }

}
