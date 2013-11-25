package pro.kornev.kcar.cop.providers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author vkornev
 * @since 14.10.13
 */
public class ConfigDB extends SQLiteOpenHelper {

    final static int DB_VER = 1;
    final static String DB_NAME = "config.db";
    final String TABLE_NAME = "config";
    final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
            "( _id INTEGER PRIMARY KEY AUTOINCREMENT, "+
            " logsEnable boolean," +
            " proxy varchar(32) )";
    final String DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;
    final String[] COLUMNS = {"_id", "logsEnable", "proxy"};
    final String SELECTIONS = "_id == ?";
    final String LIMIT = "1";

    public ConfigDB(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        initConfig(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    private void initConfig(SQLiteDatabase db) {
        if (db == null || db.isReadOnly()) {
            throw new IllegalStateException("Database " + DB_NAME + "is can't be write");
        }
        ContentValues values = new ContentValues(3);
        values.put(COLUMNS[0], 1);
        values.put(COLUMNS[1], 1);
        values.put(COLUMNS[2], "kornev.pro");
        db.insert(TABLE_NAME, null, values);
    }

    public boolean isLogsEnabled() {
        SQLiteDatabase db = getReadableDatabase();
        if (db == null) {
            throw new IllegalStateException("Database " + DB_NAME + " is not exists");
        }
        Cursor cursor = db.query(TABLE_NAME, COLUMNS, SELECTIONS, new String[]{"1"}, null, null, COLUMNS[0], LIMIT);
        if ( !cursor.moveToFirst() ) {
            return false;
        }
        boolean result = cursor.getInt(1) == 1;
        cursor.close();
        return result;
    }

    public synchronized void setLogsEnabled(boolean isLogEnabled) {
        SQLiteDatabase db = getWritableDatabase();
        if (db == null || db.isReadOnly()) {
            throw new IllegalStateException("Database " + DB_NAME + "is can't be write");
        }
        ContentValues values = new ContentValues(1);
        values.put(COLUMNS[1], isLogEnabled?1:0);
        db.update(TABLE_NAME, values, " _id = ?", new String[]{"1"});
        db.close();
    }

    public String getProxy() {
        SQLiteDatabase db = getReadableDatabase();
        if (db == null) {
            throw new IllegalStateException("Database " + DB_NAME + " is not exists");
        }
        Cursor cursor = db.query(TABLE_NAME, COLUMNS, SELECTIONS, new String[]{"1"}, null, null, COLUMNS[0], LIMIT);
        if ( !cursor.moveToFirst() ) {
            return null;
        }
        String result = cursor.getString(2);
        cursor.close();
        return result;
    }

    public synchronized void setProxy(String proxy) {
        SQLiteDatabase db = getWritableDatabase();
        if (db == null || db.isReadOnly()) {
            throw new IllegalStateException("Database " + DB_NAME + "is can't be write");
        }
        ContentValues values = new ContentValues(1);
        values.put(COLUMNS[2], proxy);
        db.update(TABLE_NAME, values, " _id = ?", new String[]{"1"});
        db.close();
    }

}