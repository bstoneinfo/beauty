package com.bstoneinfo.fashion.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bstoneinfo.lib.common.BSDBHelper;

public class MainDBHelper extends BSDBHelper {

    final static int DATABASE_VERSION = 1;

    public final static String TABLE_LIKE = "Like";
    public final static String FIELD_LIKE_ID = "likeID";
    public final static String FIELD_LIKE_KEY = "likeKey";
    public final static String FIELD_CATEGORY_ID = "categoryID";
    public final static String FIELD_LIKE_ATTRS = "attrs";

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
        sb.append(FIELD_LIKE_ATTRS).append(" TEXT)");
        db.execSQL(sb.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int getLikeID(String key) {
        int likeID = -1;
        Cursor cursor = query(TABLE_LIKE, FIELD_LIKE_KEY, key);
        if (cursor == null) {
            return -1;
        }
        if (cursor.moveToFirst()) {
            likeID = cursor.getInt(cursor.getColumnIndex(FIELD_LIKE_ID));
        } else {
            likeID = 0;
        }
        cursor.close();
        return likeID;
    }

    public void likeAdd(String categoryID, String key, String attrs, final DBExecuteListener listener) {
        ContentValues values = new ContentValues();
        values.put(FIELD_CATEGORY_ID, categoryID);
        values.put(FIELD_LIKE_KEY, key);
        values.put(FIELD_LIKE_ATTRS, attrs);
        insert(TABLE_LIKE, values, listener);
    }

    public void likeRemove(int likeID, final DBExecuteListener listener) {
        delete(TABLE_LIKE, FIELD_LIKE_ID, String.valueOf(likeID), listener);
    }

    public void likeQuery(int count, int fromID, final DBQueryListener listener) {
        String selection = null;
        String[] selectionArgs = null;
        if (fromID > 0) {
            selection = FIELD_LIKE_ID + "<?";
            selectionArgs = new String[] { String.valueOf(fromID) };
        }
        query(TABLE_LIKE, null, selection, selectionArgs, null, null, FIELD_LIKE_ID + " DESC", String.valueOf(count), listener);
    }
}
