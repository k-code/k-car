package pro.kornev.kcar.cop.providers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.SparseArray;

/**
 * @author vkornev
 * @since 14.10.13
 */
public class LogsDB extends SQLiteOpenHelper {

    final static int DB_VER = 1;
    final static String DB_NAME = "logs.db";
    final String TABLE_NAME = "logs";
    final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
            "( _id INTEGER PRIMARY KEY AUTOINCREMENT, "+
            " log TEXT )";
    final String DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;
    final String[] COLUMNS = {"_id", "log"};
    final String SELECTIONS = "_id > ?";
    final String LIMIT = "10";
    final ConfigDB config;

    public LogsDB(Context context) {
        super(context, DB_NAME, null, DB_VER);
        config = new ConfigDB(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public SparseArray<LogData> getLogs(int from) {
        SparseArray<LogData> result = new SparseArray<LogData>();

        String[] selArgs = {String.valueOf(from)};
        SQLiteDatabase db = getReadableDatabase();
        if (db == null) {
            throw new IllegalStateException("Database " + DB_NAME + " is not exists");
        }
        Cursor cursor = db.query(TABLE_NAME, COLUMNS, SELECTIONS, selArgs, null, null, COLUMNS[0], LIMIT);

        if ( !cursor.moveToFirst() ) {
            return result;
        }

        int i =0;
        do {
            LogData ld = new LogData();
            ld.setId(cursor.getInt(0));
            ld.setLog(cursor.getString(1));
            result.put(i++, ld);
        } while (cursor.moveToNext());

        cursor.close();

        return result;
    }

    public synchronized void putLog(String logData) {
        Log.w("KCAR", logData);
        if (!config.isLogsEnabled()) return;

        SQLiteDatabase db = getWritableDatabase();
        if (db == null || db.isReadOnly()) {
            throw new IllegalStateException("Database " + DB_NAME + "is can't be write");
        }
        ContentValues values = new ContentValues(1);
        values.put(COLUMNS[1], logData);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void clearLogs() {
        SQLiteDatabase db = getWritableDatabase();
        if (db == null || db.isReadOnly()) {
            throw new IllegalStateException("Database " + DB_NAME + "is can't be write");
        }
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.execSQL("DELETE FROM sqlite_sequence WHERE name = '" + TABLE_NAME +"'");
        db.close();
    }
}