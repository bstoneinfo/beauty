package com.bstoneinfo.fashion.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import com.bstoneinfo.fashion.app.AppConfig;
import com.bstoneinfo.lib.common.BSDBHelper;

public class MainDBHelper extends BSDBHelper {

    final static int DATABASE_VERSION = 1;

    public final static String TABLE_FAVORITE = "Favorite";
    public final static String FIELD_FAVORITE_ID = "favoriteID";
    public final static String FIELD_FAVORITE_KEY = "favoriteKey";
    public final static String FIELD_CATEGORY_ID = "categoryID";
    public final static String FIELD_FAVORITE_ATTRS = "attrs";
    public final static String FIELD_UPDATE_TIME = "updateTime";

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
        sb.append("CREATE TABLE IF NOT EXISTS ").append(TABLE_FAVORITE).append(" (");
        sb.append(FIELD_FAVORITE_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(FIELD_FAVORITE_KEY).append(" TEXT, ");
        sb.append(FIELD_CATEGORY_ID).append(" TEXT, ");
        sb.append(FIELD_FAVORITE_ATTRS).append(" TEXT, ");
        sb.append(FIELD_UPDATE_TIME).append(" TEXT)");
        db.execSQL(sb.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int getFavoriteID(String key) {
        int favoriteID = -1;
        Cursor cursor = query(TABLE_FAVORITE, FIELD_FAVORITE_KEY, key);
        if (cursor == null) {
            return -1;
        }
        if (cursor.moveToFirst()) {
            favoriteID = cursor.getInt(cursor.getColumnIndex(FIELD_FAVORITE_ID));
        } else {
            favoriteID = 0;
        }
        cursor.close();
        return favoriteID;
    }

    public void favoriteAdd(String categoryID, String key, String attrs, final DBExecuteListener listener) {
        final int favoriateID = getFavoriteID(key);
        if (favoriateID > 0) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    listener.finished(favoriateID);
                }
            });
            return;
        }
        ContentValues values = new ContentValues();
        values.put(FIELD_CATEGORY_ID, categoryID);
        values.put(FIELD_FAVORITE_KEY, key);
        values.put(FIELD_FAVORITE_ATTRS, attrs);
        values.put(FIELD_UPDATE_TIME, AppConfig.getServerTime());
        insert(TABLE_FAVORITE, values, listener);
    }

    public void favoriteRemove(int favoriteID, final DBExecuteListener listener) {
        delete(TABLE_FAVORITE, FIELD_FAVORITE_ID, String.valueOf(favoriteID), listener);
    }

    public void favoriteQuery(int count, int fromID, final DBQueryListener listener) {
        String selection = null;
        String[] selectionArgs = null;
        if (fromID > 0) {
            selection = FIELD_FAVORITE_ID + "<?";
            selectionArgs = new String[] { String.valueOf(fromID) };
        }
        query(TABLE_FAVORITE, null, selection, selectionArgs, null, null, FIELD_FAVORITE_ID + " DESC", String.valueOf(count), listener);
    }
}
