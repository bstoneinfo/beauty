package com.bstoneinfo.fashion.data;

import org.json.JSONObject;

import com.bstoneinfo.fashion.app.MyUtils;

public class CategoryItemData {

    public int id;

    public String thumbURL;
    public int thumbWidth, thumbHeight;

    public String standardURL;
    public int standardWidth, standardHeight;

    public CategoryItemData(JSONObject jsonItem) {
        String host = "http://" + MyUtils.getHost();
        thumbURL = host + jsonItem.optString("thumb_url");
        thumbWidth = jsonItem.optInt("thumb_width");
        thumbHeight = jsonItem.optInt("thumb_height");
        standardURL = host + jsonItem.optString("url");
        standardWidth = jsonItem.optInt("width");
        standardHeight = jsonItem.optInt("height");
    }

    @Override
    public String toString() {
        return standardURL;
    }

}
