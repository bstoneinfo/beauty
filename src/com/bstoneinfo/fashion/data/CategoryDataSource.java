package com.bstoneinfo.fashion.data;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.os.Handler;

import com.bstoneinfo.fashion.app.MyUtils;
import com.bstoneinfo.lib.common.BSApplication;
import com.bstoneinfo.lib.common.BSLog;
import com.bstoneinfo.lib.common.BSNotificationCenter;
import com.bstoneinfo.lib.common.BSUtils;
import com.bstoneinfo.lib.net.BSJsonConnection;
import com.bstoneinfo.lib.net.BSJsonConnection.BSJsonConnectionListener;

public class CategoryDataSource {

    public static final String CATEGORY_EXPLORE_FINISHED = "CATEGORY_EXPLORE_FINISHED_";
    public static final String CATEGORY_HISTORY_FINISHED = "CATEGORY_HISTORY_FINISHED_";

    private final String categoryName;
    private final int groupSize;
    private final BSNotificationCenter notificationCenter;

    private JSONArray histroyJsonArray;
    private int nextHistroyIndex = 0;
    private int nextExploreGroup = -1;
    private BSJsonConnection exploreJsonConnection;
    private BSJsonConnection histroyJsonConnection;
    private boolean isLoadingExplore = false, isLoadingHistroy = false;

    public CategoryDataSource(String name, BSNotificationCenter notificationCenter) {
        this.categoryName = name;
        this.notificationCenter = notificationCenter;
        JSONObject sizeJson = BSApplication.getApplication().getRemoteConfig().optJSONObject("CategorySize");
        if (sizeJson != null) {
            groupSize = sizeJson.optInt(categoryName, 30);
        } else {
            groupSize = 30;
        }

        //从历史记录中读取信息
        try {
            histroyJsonArray = new JSONArray(getPreferences().getString("histroy", ""));
        } catch (Exception e) {
            histroyJsonArray = new JSONArray();
        }
        nextHistroyIndex = histroyJsonArray.length() - 2;
        try {
            nextExploreGroup = histroyJsonArray.getInt(histroyJsonArray.length() - 1);
        } catch (Exception e) {
            nextExploreGroup = groupSize;
        }
    }

    protected SharedPreferences getPreferences() {
        return BSApplication.getApplication().getSharedPreferences("category_" + categoryName, 0);
    }

    public void exploreMore() {
        if (isLoadingExplore) {
            return;
        }
        isLoadingExplore = true;
        BSLog.d("nextExploreGroup=" + nextExploreGroup);
        final String relativePath = "/fashion/" + categoryName + "/v2/" + nextExploreGroup + ".json";
        final ArrayList<CategoryItemData> dataList;
        if (nextExploreGroup <= 0) {
            dataList = new ArrayList<CategoryItemData>();
        } else {
            dataList = loadJsonDataFromLocal(relativePath);
        }
        if (dataList != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    notifyExploreFinished(dataList);
                }
            }, 100);
        } else {
            final String urlString = "http://" + MyUtils.getHost() + relativePath;
            exploreJsonConnection = new BSJsonConnection(urlString);
            exploreJsonConnection.start(new BSJsonConnectionListener() {
                @Override
                public void finished(JSONObject jsonObject) {
                    BSLog.d(urlString + " success");
                    ArrayList<CategoryItemData> dataList = loadJsonData(jsonObject);
                    if (dataList != null && dataList.size() > 0) {
                        BSUtils.saveStringToFile(jsonObject.toString(), BSUtils.getCachePath(relativePath));
                    }
                    notifyExploreFinished(dataList);
                }

                @Override
                public void failed(Exception exception) {
                    BSLog.d(urlString + " failed " + exception.toString());
                    notifyExploreFinished(null);
                }
            });
        }
    }

    private void notifyExploreFinished(ArrayList<CategoryItemData> dataList) {
        notificationCenter.notifyOnUIThread(CATEGORY_EXPLORE_FINISHED + categoryName, dataList);
        isLoadingExplore = false;
        if (dataList == null) {
            return;
        }
        JSONArray newHistroyJsonArray = new JSONArray();
        for (int i = 0; i < histroyJsonArray.length(); i++) {
            int group = histroyJsonArray.optInt(i);
            if (group != nextExploreGroup) {
                newHistroyJsonArray.put(group);
            }
        }
        histroyJsonArray = newHistroyJsonArray;
        if (histroyJsonArray.length() > 0 && nextExploreGroup == histroyJsonArray.optInt(histroyJsonArray.length() - 1)) {
            nextExploreGroup = groupSize;//是历史项的第一个，下一个从头开始读
        } else {
            histroyJsonArray.put(nextExploreGroup);
            nextExploreGroup--;
        }
        getPreferences().edit().putString("histroy", histroyJsonArray.toString()).commit();

        //计算下一个nextExploreGroup
        while (nextExploreGroup > 0) {
            int i = 0;
            for (; i < histroyJsonArray.length(); i++) {
                if (histroyJsonArray.optInt(i) == nextExploreGroup) {
                    break;
                }
            }
            if (i == histroyJsonArray.length()) {
                break;
            }
            nextExploreGroup--;
        }
    }

    public void histroyMore() {
        if (isLoadingHistroy) {
            return;
        }
        isLoadingHistroy = true;
        BSLog.d("nextHistroyIndex=" + nextHistroyIndex);
        final String relativePath = "/fashion/" + categoryName + "/v2/" + histroyJsonArray.optInt(nextHistroyIndex) + ".json";
        final ArrayList<CategoryItemData> dataList;
        if (nextHistroyIndex <= 0) {
            dataList = new ArrayList<CategoryItemData>();
        } else {
            dataList = loadJsonDataFromLocal(relativePath);
        }
        if (dataList != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    notifyHistroyFinished(dataList);
                }
            }, 100);
        } else {
            final String urlString = "http://" + MyUtils.getHost() + relativePath;
            histroyJsonConnection = new BSJsonConnection(urlString);
            histroyJsonConnection.start(new BSJsonConnectionListener() {
                @Override
                public void finished(JSONObject jsonObject) {
                    BSLog.d(urlString + " success");
                    ArrayList<CategoryItemData> dataList = loadJsonData(jsonObject);
                    if (dataList != null && dataList.size() > 0) {
                        BSUtils.saveStringToFile(jsonObject.toString(), BSUtils.getCachePath(relativePath));
                    }
                    notifyHistroyFinished(dataList);
                }

                @Override
                public void failed(Exception exception) {
                    BSLog.d(urlString + " failed " + exception.toString());
                    notifyHistroyFinished(null);
                }
            });
        }
    }

    private void notifyHistroyFinished(ArrayList<CategoryItemData> dataList) {
        notificationCenter.notifyOnUIThread(CATEGORY_HISTORY_FINISHED + categoryName, dataList);
        isLoadingHistroy = false;
        if (dataList == null) {
            return;
        }
        nextHistroyIndex--;
    }

    private ArrayList<CategoryItemData> loadJsonDataFromLocal(String relPath) {
        String cachePath = BSUtils.getCachePath(relPath);
        if (new File(cachePath).exists()) {
            String content = BSUtils.readStringFromFile(cachePath);
            try {
                JSONObject jsonObject = new JSONObject(content);
                return loadJsonData(jsonObject);
            } catch (JSONException e) {
            }
        }
        return null;
    }

    private ArrayList<CategoryItemData> loadJsonData(final JSONObject jsonObject) {
        try {
            ArrayList<CategoryItemData> dataList = new ArrayList<CategoryItemData>();
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonItem = jsonArray.getJSONObject(i);
                CategoryItemData itemData = new CategoryItemData();
                itemData.thumbURL = jsonItem.getString("thumb_url");
                itemData.thumbWidth = jsonItem.getInt("thumb_width");
                itemData.thumbHeight = jsonItem.getInt("thumb_height");
                itemData.standardURL = jsonItem.getString("url");
                itemData.standardWidth = jsonItem.getInt("width");
                itemData.standardHeight = jsonItem.getInt("height");
                dataList.add(itemData);
            }
            return dataList;
        } catch (JSONException e) {
        }
        return null;
    }

}
