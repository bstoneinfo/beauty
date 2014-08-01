package com.bstoneinfo.fashion.favorite;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

import com.bstoneinfo.fashion.data.CategoryItemData;
import com.bstoneinfo.fashion.data.MainDBHelper;
import com.bstoneinfo.lib.common.BSDBHelper.DBExecuteListener;
import com.bstoneinfo.lib.common.BSDBHelper.DBQueryListener;

public class FavoriteAgent {

    private boolean canceled = false;

    public interface FavoriteUpdateListener {
        public void finished(boolean success);
    }

    public interface FavoriteQueryListener {
        public void finished(ArrayList<CategoryItemData> itemList);
    }

    public boolean isFavorite(CategoryItemData item) {
        if (item.favoriteID > 0) {
            return true;
        } else if (item.favoriteID == 0) {
            return false;
        }
        item.favoriteID = MainDBHelper.getSingleton().getFavoriteID(item.standardURL);
        return item.favoriteID > 0;
    }

    public void favoriteQuery(int count, int fromID, final FavoriteQueryListener listener) {
        MainDBHelper.getSingleton().favoriteQuery(count, fromID, new DBQueryListener() {
            @Override
            public void finished(Cursor cursor) {
                if (canceled) {
                    return;
                }
                ArrayList<CategoryItemData> itemList = new ArrayList<CategoryItemData>();
                if (cursor == null) {
                    listener.finished(null);
                    return;
                }
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    String favoriteJson = cursor.getString(cursor.getColumnIndex(MainDBHelper.FIELD_FAVORITE_ATTRS));
                    try {
                        JSONObject jsonObject = new JSONObject(favoriteJson);
                        String category = cursor.getString(cursor.getColumnIndex(MainDBHelper.FIELD_CATEGORY_ID));
                        CategoryItemData item = new CategoryItemData(category, jsonObject);
                        item.favoriteID = cursor.getInt(cursor.getColumnIndex(MainDBHelper.FIELD_FAVORITE_ID));
                        itemList.add(item);
                    } catch (JSONException e) {
                    }
                }
                listener.finished(itemList);
            }
        });
    }

    public void favoriteAdd(final CategoryItemData item, final FavoriteUpdateListener listener) {
        MainDBHelper.getSingleton().favoriteAdd(item.category, item.standardURL, item.jsonItem.toString(), new DBExecuteListener() {
            @Override
            public void finished(long result) {
                if (result > 0) {
                    item.favoriteID = (int) result;
                }
                if (listener != null && !canceled) {
                    listener.finished(result > 0);
                }
            }
        });
    }

    public void favoriteRemove(final CategoryItemData item, final FavoriteUpdateListener listener) {
        MainDBHelper.getSingleton().favoriteRemove(item.favoriteID, new DBExecuteListener() {
            @Override
            public void finished(long result) {
                if (result > 0) {
                    item.favoriteID = 0;
                }
                if (listener != null && !canceled) {
                    listener.finished(result > 0);
                }
            }
        });
    }

    public void cancel() {
        canceled = true;
    }
}
