package com.bstoneinfo.fashion.app;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

import com.bstoneinfo.lib.common.BSApplication;

public class MyUtils {

    private static String host = null;

    public static String getHost() {
        if (host != null) {
            return host;
        }
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
