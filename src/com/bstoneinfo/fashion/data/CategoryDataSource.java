package com.bstoneinfo.fashion.data;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.os.Handler;

import com.bstoneinfo.fashion.app.MyUtils;
import com.bstoneinfo.fashion.app.NotificationEvent;
import com.bstoneinfo.lib.common.BSApplication;
import com.bstoneinfo.lib.common.BSLog;
import com.bstoneinfo.lib.common.BSUtils;
import com.bstoneinfo.lib.net.BSJsonConnection;
import com.bstoneinfo.lib.net.BSJsonConnection.BSJsonConnectionListener;

public class CategoryDataSource {

    private final String categoryName;
    private final int groupSize;

    private JSONArray histroyJsonArray;
    private final int[] histroyGroupArray;
    private int nextHistroyIndex = 0;
    private int nextExploreGroup = -1;
    private BSJsonConnection exploreJsonConnection;
    private BSJsonConnection histroyJsonConnection;
    private boolean isLoadingExplore = false, isLoadingHistroy = false;

    public CategoryDataSource(String name) {
        this.categoryName = name;
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
        if (histroyJsonArray.length() > 1) {
            histroyGroupArray = new int[histroyJsonArray.length() - 1];
            for (int i = 0; i < histroyJsonArray.length() - 1; i++) {
                histroyGroupArray[i] = histroyJsonArray.optInt(histroyJsonArray.length() - i - 2);
            }
        } else {
            histroyGroupArray = null;
        }
        nextHistroyIndex = 0;
        try {
            nextExploreGroup = histroyJsonArray.getInt(histroyJsonArray.length() - 1);
            if (nextExploreGroup <= 0) {
                nextExploreGroup = groupSize;
            }
        } catch (Exception e) {
            nextExploreGroup = groupSize;
        }
    }

    public void destroy() {
        if (exploreJsonConnection != null) {
            exploreJsonConnection.cancel();
            exploreJsonConnection = null;
        }
        if (histroyJsonConnection != null) {
            histroyJsonConnection.cancel();
            histroyJsonConnection = null;
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
        BSApplication.defaultNotificationCenter.notifyOnUIThread(NotificationEvent.CATEGORY_EXPLORE_FINISHED_ + categoryName, dataList);
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
        histroyJsonArray.put(nextExploreGroup);
        getPreferences().edit().putString("histroy", histroyJsonArray.toString()).commit();

        //计算下一个nextExploreGroup
        nextExploreGroup--;
        if (nextExploreGroup == 0) {
            nextExploreGroup = groupSize;
        }
        //以下代码是为了找到下一个未看到过的页
        if (histroyGroupArray != null) {
            int newNextGroup = nextExploreGroup;
            while (newNextGroup > 0) {
                int i = 0;
                for (; i < histroyJsonArray.length(); i++) {
                    if (histroyJsonArray.optInt(i) == nextExploreGroup) {
                        break;
                    }
                }
                if (i == histroyJsonArray.length()) {
                    nextExploreGroup = newNextGroup;
                    break;
                }
                newNextGroup--;
            }
        }
    }

    public void histroyMore() {
        if (isLoadingHistroy) {
            return;
        }
        isLoadingHistroy = true;
        BSLog.d("nextHistroyIndex=" + nextHistroyIndex);
        final String relativePath;
        final ArrayList<CategoryItemData> dataList;
        if (nextHistroyIndex < 0 || histroyGroupArray == null || nextHistroyIndex >= histroyGroupArray.length) {
            relativePath = "";
            dataList = new ArrayList<CategoryItemData>();
        } else {
            relativePath = "/fashion/" + categoryName + "/v2/" + histroyGroupArray[nextHistroyIndex] + ".json";
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
        BSApplication.defaultNotificationCenter.notifyOnUIThread(NotificationEvent.CATEGORY_HISTORY_FINISHED_ + categoryName, dataList);
        isLoadingHistroy = false;
        if (dataList == null || dataList.isEmpty()) {
            return;
        }
        nextHistroyIndex++;
        if (nextHistroyIndex >= histroyGroupArray.length) {
            BSApplication.defaultNotificationCenter.notifyOnUIThread(NotificationEvent.CATEGORY_HISTORY_FINISHED_ + categoryName, new ArrayList<CategoryItemData>());
        }
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
                CategoryItemData itemData = new CategoryItemData(categoryName, jsonItem);
                dataList.add(itemData);
            }
            return dataList;
        } catch (JSONException e) {
        }
        return null;
    }

}
