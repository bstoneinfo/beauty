package com.bstoneinfo.fashion.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bstoneinfo.lib.common.BSDBHelper;

public class MainDBHelper extends BSDBHelper {

    final static int DATABASE_VERSION = 1;

    final static String TABLE_LIKE = "Like";
    final static String FIELD_LIKE_ID = "likeID";
    final static String FIELD_CATEGORY_ID = "categoryID";
    final static String FIELD_LIKE_JSON = "json";

    public MainDBHelper(Context context, int version) {
        super(context, "main", version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE IF NOT EXISTS ").append(TABLE_LIKE).append(" (");
        sb.append(FIELD_LIKE_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(FIELD_CATEGORY_ID).append(" TEXT, ");
        sb.append(FIELD_LIKE_JSON).append(" TEXT)");
        db.execSQL(sb.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //    public ArrayList<JSONObject> likeAdd(String categoryID, JSONObject jsonItem) {
    //
    //    }
    //
    //    public ArrayList<JSONObject> likeRemove(int id) {
    //
    //    }
    //
    //    public ArrayList<JSONObject> likeQuery() {
    //
    //    }

}
