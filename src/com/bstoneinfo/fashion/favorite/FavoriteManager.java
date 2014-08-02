package com.bstoneinfo.fashion.favorite;

import java.util.ArrayList;

import org.json.JSONObject;

import android.database.Cursor;
import android.os.Handler;

import com.bstoneinfo.fashion.app.NotificationEvent;
import com.bstoneinfo.fashion.data.CategoryItemData;
import com.bstoneinfo.fashion.data.MainDBHelper;
import com.bstoneinfo.lib.common.BSApplication;
import com.bstoneinfo.lib.common.BSDBHelper.DBExecuteListener;
import com.bstoneinfo.lib.common.BSDBHelper.DBQueryListener;

public class FavoriteManager {

    private final static int LOAD_MORE_COUNT = 10;
    private int nextID = 0;
    private final static FavoriteManager instance = new FavoriteManager();

    public static FavoriteManager getInstance() {
        return instance;
    }

    public boolean isFavorite(CategoryItemData item) {
        if (item.favoriteID > 0) {
            return true;
        } else if (item.favoriteID == 0) {
            return false;
        }
        item.favoriteID = MainDBHelper.getSingleton().getFavoriteID(item.getFavoriteKey());
        return item.favoriteID > 0;
    }

    public void favoriteAdd(final CategoryItemData item) {
        MainDBHelper.getSingleton().favoriteAdd(item.category, item.getFavoriteKey(), item.jsonItem.toString(), new DBExecuteListener() {
            @Override
            public void finished(long result) {
                if (result > 0) {
                    item.favoriteID = (int) result;
                }
                BSApplication.defaultNotificationCenter.notifyOnUIThread(NotificationEvent.CATEGORY_ITEM_DATA_FINISHED, item);
            }
        });
    }

    public void favoriteRemove(final CategoryItemData item) {
        MainDBHelper.getSingleton().favoriteRemove(item.favoriteID, new DBExecuteListener() {
            @Override
            public void finished(long result) {
                if (result > 0) {
                    item.favoriteID = 0;
                }
                BSApplication.defaultNotificationCenter.notifyOnUIThread(NotificationEvent.CATEGORY_ITEM_DATA_FINISHED, item);
            }
        });
    }

    public void favoriteMore() {
        if (nextID < 0) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    BSApplication.defaultNotificationCenter.notifyOnUIThread(NotificationEvent.FAVORITE_QUERYLIST_FINISHED, new ArrayList<CategoryItemData>());
                }
            });
            return;
        }
        MainDBHelper.getSingleton().favoriteQuery(LOAD_MORE_COUNT + 1, nextID, new DBQueryListener() {
            @Override
            public void finished(Cursor cursor) {
                ArrayList<CategoryItemData> itemList = new ArrayList<CategoryItemData>();
                if (cursor == null) {
                    BSApplication.defaultNotificationCenter.notifyOnUIThread(NotificationEvent.FAVORITE_QUERYLIST_FINISHED, null);
                    return;
                }
                for (cursor.moveToFirst(); !cursor.isAfterLast() && itemList.size() < LOAD_MORE_COUNT; cursor.moveToNext()) {
                    String favoriteJson = cursor.getString(cursor.getColumnIndex(MainDBHelper.FIELD_FAVORITE_ATTRS));
                    try {
                        JSONObject jsonObject = new JSONObject(favoriteJson);
                        String category = cursor.getString(cursor.getColumnIndex(MainDBHelper.FIELD_CATEGORY_ID));
                        CategoryItemData item = new CategoryItemData(category, jsonObject);
                        item.favoriteID = cursor.getInt(cursor.getColumnIndex(MainDBHelper.FIELD_FAVORITE_ID));
                        itemList.add(item);
                    } catch (Exception e) {
                    }
                }
                if (cursor.getCount() < LOAD_MORE_COUNT + 1) {
                    nextID = -1;
                } else {
                    nextID = itemList.get(itemList.size() - 1).favoriteID;
                }
                BSApplication.defaultNotificationCenter.notifyOnUIThread(NotificationEvent.FAVORITE_QUERYLIST_FINISHED, itemList);
                if (!itemList.isEmpty() && nextID < 0) {
                    BSApplication.defaultNotificationCenter.notifyOnUIThread(NotificationEvent.FAVORITE_QUERYLIST_FINISHED, new ArrayList<CategoryItemData>());
                }
            }
        });
    }

    public void reset() {
        nextID = 0;
    }
}
