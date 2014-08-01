package com.bstoneinfo.fashion.like;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;

import com.bstoneinfo.fashion.data.CategoryItemData;
import com.bstoneinfo.fashion.data.MainDBHelper;
import com.bstoneinfo.lib.common.BSDBHelper.DBExecuteListener;
import com.bstoneinfo.lib.common.BSDBHelper.DBQueryListener;

public class LikeAgent {

    private boolean canceled = false;

    public interface LikeUpdateListener {
        public void finished(boolean success);
    }

    public interface LikeQueryListener {
        public void finished(ArrayList<CategoryItemData> itemList);
    }

    public boolean isLike(CategoryItemData item) {
        if (item.likeID > 0) {
            return true;
        } else if (item.likeID == 0) {
            return false;
        }
        item.likeID = MainDBHelper.getSingleton().getLikeID(item.standardURL);
        return item.likeID > 0;
    }

    public void likeQuery(int count, int fromID, final LikeQueryListener listener) {
        MainDBHelper.getSingleton().likeQuery(count, fromID, new DBQueryListener() {
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
                    String likeJson = cursor.getString(cursor.getColumnIndex(MainDBHelper.FIELD_LIKE_ATTRS));
                    try {
                        JSONObject jsonObject = new JSONObject(likeJson);
                        String category = cursor.getString(cursor.getColumnIndex(MainDBHelper.FIELD_CATEGORY_ID));
                        CategoryItemData item = new CategoryItemData(category, jsonObject);
                        item.likeID = cursor.getInt(cursor.getColumnIndex(MainDBHelper.FIELD_LIKE_ID));
                        itemList.add(item);
                    } catch (JSONException e) {
                    }
                }
                listener.finished(itemList);
            }
        });
    }

    public void likeAdd(final CategoryItemData item, final LikeUpdateListener listener) {
        MainDBHelper.getSingleton().likeAdd(item.category, item.standardURL, item.jsonItem.toString(), new DBExecuteListener() {
            @Override
            public void finished(long result) {
                if (result > 0) {
                    item.likeID = (int) result;
                    if (listener != null) {
                        listener.finished(true);
                    }
                } else {
                    if (listener != null) {
                        listener.finished(false);
                    }
                }
            }
        });
    }

    public void likeRemove(final CategoryItemData item, final LikeUpdateListener listener) {
        MainDBHelper.getSingleton().likeRemove(item.likeID, new DBExecuteListener() {
            @Override
            public void finished(long result) {
                if (listener != null) {
                    if (result > 0) {
                        item.likeID = 0;
                    }
                    listener.finished(result > 0);
                }
            }
        });
    }

    public void cancel() {
        canceled = true;
    }
}
