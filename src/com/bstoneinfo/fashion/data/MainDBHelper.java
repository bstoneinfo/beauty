package com.bstoneinfo.fashion.data;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bstoneinfo.lib.common.BSDBHelper;
import com.bstoneinfo.lib.common.BSUtils;

public class MainDBHelper extends BSDBHelper {

    public interface DBResultListener {
        public void finished(boolean success);
    }

    final static int DATABASE_VERSION = 1;

    final static String TABLE_LIKE = "Like";
    final static String FIELD_LIKE_ID = "likeID";
    final static String FIELD_LIKE_KEY = "likeKey";
    final static String FIELD_CATEGORY_ID = "categoryID";
    final static String FIELD_LIKE_JSON = "json";

    private static MainDBHelper instance;

    public static MainDBHelper getSingleton() {
        return instance;
    }

    public static void createSingleton(Context context) {
        if (instance == null) {
            instance = new MainDBHelper(context, DATABASE_VERSION);
        }
    }

    public MainDBHelper(Context context, int version) {
        super(context, "main", version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(TABLE_LIKE).append(" (");
        sb.append(FIELD_LIKE_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(FIELD_LIKE_KEY).append(" TEXT, ");
        sb.append(FIELD_CATEGORY_ID).append(" TEXT, ");
        sb.append(FIELD_LIKE_JSON).append(" TEXT)");
        db.execSQL(sb.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean isLike(CategoryItemData item) {
        if (item.likeID > 0) {
            return true;
        } else if (item.likeID == 0) {
            return false;
        }
        Cursor cursor = query(TABLE_LIKE, FIELD_LIKE_KEY, item.standardURL);
        if (cursor == null) {
            return false;
        }
        item.likeID = 0;
        if (cursor.moveToFirst()) {
            item.likeID = cursor.getInt(cursor.getColumnIndex(FIELD_LIKE_ID));
        }
        cursor.close();
        return item.likeID > 0;
    }

    public void likeAdd(String categoryID, final CategoryItemData item, final DBResultListener listener) {
        if (item.likeID > 0) {
            BSUtils.debugAssert("item has been liked. " + item.toString());
            return;
        }
        ContentValues values = new ContentValues();
        values.put(FIELD_CATEGORY_ID, categoryID);
        values.put(FIELD_LIKE_KEY, item.standardURL);
        values.put(FIELD_LIKE_JSON, item.jsonItem.toString());
        insert(TABLE_LIKE, values, new DBExecuteListener() {
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

    public void likeRemove(final CategoryItemData item, final DBResultListener listener) {
        if (item.likeID <= 0) {
            BSUtils.debugAssert("item has not been liked. " + item.toString());
            return;
        }
        delete(TABLE_LIKE, FIELD_LIKE_ID, String.valueOf(item.likeID), new DBExecuteListener() {
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

    public interface LikeQueryListener {
        public void finished(ArrayList<CategoryItemData> itemList);
    }

    public void likeQuery(int count, int fromID, final LikeQueryListener listener) {
        String selection = null;
        String[] selectionArgs = null;
        if (fromID > 0) {
            selection = FIELD_LIKE_ID + "<?";
            selectionArgs = new String[] { String.valueOf(fromID) };
        }
        query(TABLE_LIKE, null, selection, selectionArgs, null, null, FIELD_LIKE_ID + " DESC", String.valueOf(count), new DBQueryListener() {
            @Override
            public void finished(Cursor cursor) {
                ArrayList<CategoryItemData> itemList = new ArrayList<CategoryItemData>();
                if (cursor == null) {
                    listener.finished(null);
                    return;
                }
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    String likeJson = cursor.getString(cursor.getColumnIndex(FIELD_LIKE_JSON));
                    try {
                        JSONObject jsonObject = new JSONObject(likeJson);
                        String category = cursor.getString(cursor.getColumnIndex(FIELD_CATEGORY_ID));
                        CategoryItemData item = new CategoryItemData(category, jsonObject);
                        item.likeID = cursor.getInt(cursor.getColumnIndex(FIELD_LIKE_ID));
                        itemList.add(item);
                    } catch (JSONException e) {
                    }
                }
                listener.finished(itemList);
            }
        });
    }
}
