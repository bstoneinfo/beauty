package com.bstoneinfo.fashion.app;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

import com.bstoneinfo.lib.common.BSApplication;

public class MyUtils {
    public static String getHost() {
        String host = null;
        JSONObject jsonConfig = BSApplication.getApplication().getRemoteConfig();
        JSONArray jsonArray = jsonConfig.optJSONArray("server");
        if (jsonArray != null && jsonArray.length() > 0) {
            int index = 0;
            if (jsonArray.length() > 1) {
                index = (int) (Math.random() * jsonArray.length());
            }
            host = jsonArray.optString(index);
        }
        if (TextUtils.isEmpty(host)) {
            host = "www.bstoneinfo.com";
        }
        return host;
    }
}
