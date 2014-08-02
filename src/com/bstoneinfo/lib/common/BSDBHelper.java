/**
 *
 */
package com.bstoneinfo.lib.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;

public abstract class BSDBHelper extends SQLiteOpenHelper {

    public interface DBExecuteListener {
        public void finished(long result);
    }

    public interface DBQueryListener {
        public void finished(Cursor cursor);
    }

    public BSDBHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    protected void notifyListener(Handler handler, final DBExecuteListener listener, final long result) {
        if (listener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.finished(result);
                }
            });
        }
    }

    protected void notifyListener(Handler handler, final DBQueryListener listener, final Cursor cursor) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.finished(cursor);
                if (cursor != null) {
                    cursor.close();
                }
            }
        });
    }

    public void execute(final String sql, final DBExecuteListener listener) {
        execute(sql, null, listener);
    }

    public void execute(final String sql, final Object[] bindArgs, final DBExecuteListener listener) {
        final Handler handler = new Handler();
        BSApplication.databaseThread.run(new Runnable() {
            @Override
            public void run() {
                try {
                    getWritableDatabase().execSQL(sql, bindArgs);
                    notifyListener(handler, listener, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void insert(final String table, final ContentValues values, final DBExecuteListener listener) {
        final Handler handler = new Handler();
        BSApplication.databaseThread.run(new Runnable() {
            @Override
            public void run() {
                try {
                    long result = getWritableDatabase().insert(table, null, values);
                    notifyListener(handler, listener, result);
                } catch (Exception e) {
                    notifyListener(handler, listener, -1);
                }
            }
        });
    }

    public void insertOrIgnore(final String table, final ContentValues values, final DBExecuteListener listener) {
        final Handler handler = new Handler();
        BSApplication.databaseThread.run(new Runnable() {
            @Override
            public void run() {
                try {
                    long result = getWritableDatabase().insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                    notifyListener(handler, listener, result);
                } catch (Exception e) {
                    notifyListener(handler, listener, -1);
                }
            }
        });
    }

    public void insertOrReplace(final String table, final ContentValues values, final DBExecuteListener listener) {
        final Handler handler = new Handler();
        BSApplication.databaseThread.run(new Runnable() {
            @Override
            public void run() {
                try {
                    long result = getWritableDatabase().insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                    notifyListener(handler, listener, result);
                } catch (Exception e) {
                    notifyListener(handler, listener, -1);
                }
            }
        });
    }

    public void update(String table, ContentValues values, String whereClause, DBExecuteListener listener) {
        update(table, values, whereClause, null, listener);
    }

    public void update(String table, String keyField, ContentValues values, DBExecuteListener listener) {
        String keyValue = values.getAsString(keyField);
        update(table, values, keyField + "=?", new String[] { keyValue }, listener);
    }

    public void update(String table, String keyField, String keyValue, ContentValues values, DBExecuteListener listener) {
        update(table, values, keyField + "=?", new String[] { keyValue }, listener);
    }

    public void update(final String table, final ContentValues values, final String whereClause, final String[] whereArgs, final DBExecuteListener listener) {
        final Handler handler = new Handler();
        BSApplication.databaseThread.run(new Runnable() {
            @Override
            public void run() {
                try {
                    long result = getWritableDatabase().update(table, values, whereClause, whereArgs);
                    notifyListener(handler, listener, result);
                } catch (Exception e) {
                    notifyListener(handler, listener, -1);
                }
            }
        });
    }

    public void delete(String table, String whereClause, DBExecuteListener listener) {
        delete(table, whereClause, (String[]) null, listener);
    }

    public void delete(String table, String keyField, String keyValue, DBExecuteListener listener) {
        delete(table, keyField + "=?", new String[] { keyValue }, listener);
    }

    public void delete(final String table, final String whereClause, final String[] whereArgs, final DBExecuteListener listener) {
        final Handler handler = new Handler();
        BSApplication.databaseThread.run(new Runnable() {
            @Override
            public void run() {
                try {
                    int result = getWritableDatabase().delete(table, whereClause, whereArgs);
                    notifyListener(handler, listener, result);
                } catch (Exception e) {
                    notifyListener(handler, listener, -1);
                }
            }
        });
    }

    public int getRecordCount(String table) {
        return getRecordCount(table, null, null);
    }

    public int getRecordCount(String table, String selection) {
        return getRecordCount(table, selection, null);
    }

    public int getRecordCount(String table, String selection, String[] selectionArgs) {
        int count = 0;
        Cursor c = null;
        c = query(table, new String[] { "COUNT(*)" }, selection, selectionArgs);
        if (c != null && c.moveToFirst()) {
            count = c.getInt(0);
        }
        if (c != null) {
            c.close();
        }
        return count;
    }

    public Cursor rawQuery(String sql) {
        return rawQuery(sql, null);
    }

    public Cursor rawQuery(String sql, String[] selectionArgs) {
        return getReadableDatabase().rawQuery(sql, selectionArgs);
    }

    public Cursor query(String table, String keyField, String keyValue) {
        return query(table, null, keyField + "=?", new String[] { keyValue }, null, null, null, null, false);
    }

    public Cursor query(String table, String[] columns, String keyField, String keyValue) {
        return query(table, columns, keyField + "=?", new String[] { keyValue }, null, null, null, null, false);
    }

    public Cursor query(String table) {
        return query(table, null, null, null, null, null, null, null, false);
    }

    public Cursor query(String table, String[] columns) {
        return query(table, columns, null, null, null, null, null, null, false);
    }

    public Cursor query(String table, String[] columns, String selection) {
        return query(table, columns, selection, null, null, null, null, null, false);
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs) {
        return query(table, columns, selection, selectionArgs, null, null, null, null, false);
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return query(table, columns, selection, selectionArgs, groupBy, having, orderBy, null, false);
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit, false);
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, boolean distinct) {
        return getReadableDatabase().query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    public void query(final String table, final String[] columns, final String selection, final String[] selectionArgs, final String groupBy, final String having,
            final String orderBy, final String limit, final DBQueryListener listener) {
        final Handler handler = new Handler();
        BSApplication.databaseThread.run(new Runnable() {
            @Override
            public void run() {
                try {
                    Cursor cursor = query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit, false);
                    notifyListener(handler, listener, cursor);
                } catch (Exception e) {
                    BSLog.e(e.getMessage());
                    notifyListener(handler, listener, null);
                }
            }
        });
    }

    /**
     * 判断某张表中是否存在某字段
     * 
     * @param tableName 表名
     * @param columnName 字段名
     * @return
     */
    public boolean isColumnExist(String tableName, String columnName) {
        boolean result = false;
        Cursor cursor = null;
        try {
            String sql = "select count(1) as c from sqlite_master where type ='table' and name ='" + tableName.trim() + "' and sql like '%" + columnName.trim() + "%'";
            cursor = rawQuery(sql);
            if (cursor != null && cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public String getAddColumnSQL(String tableName, String columnName, String type) {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER TABLE ").append(tableName).append(" ADD COLUMN ").append(columnName).append(" ").append(type);
        return sb.toString();
    }

    public void beginTransaction() {
        getWritableDatabase().beginTransaction();
    }

    public void setTransactionSuccessful() {
        getWritableDatabase().setTransactionSuccessful();
    }

    public void endTransaction() {
        getWritableDatabase().endTransaction();
    }
}
