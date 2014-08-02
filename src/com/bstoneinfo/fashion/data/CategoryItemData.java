package com.bstoneinfo.fashion.data;

import org.json.JSONObject;

public class CategoryItemData {

    public String category;
    public int favoriteID = -1;

    public String thumbURL;
    public int thumbWidth, thumbHeight;

    public String standardURL;
    public int standardWidth, standardHeight;

    public JSONObject jsonItem;

    public CategoryItemData(String category) {
        this.category = category;
    }

    public CategoryItemData(String category, JSONObject jsonItem) {
        this.category = category;
        this.jsonItem = jsonItem;
        thumbURL = jsonItem.optString("thumb_url");
        thumbWidth = jsonItem.optInt("thumb_width");
        thumbHeight = jsonItem.optInt("thumb_height");
        standardURL = jsonItem.optString("url");
        standardWidth = jsonItem.optInt("width");
        standardHeight = jsonItem.optInt("height");
    }

    public String getFavoriteKey() {
        return standardURL;
    }

    @Override
    public String toString() {
        return standardURL;
    }

}
