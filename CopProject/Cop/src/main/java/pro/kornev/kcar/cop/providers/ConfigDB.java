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

    private static final int DB_VER = 2;
    private static final String DB_NAME = "config.db";
    private static final String TABLE_NAME = "config";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;
    private static final String SELECTIONS = "_id == ?";
    private static final String[] SELECTION_ARGS = {"1"};
    private static final String LIMIT = "1";
    private static final String ID_COLUMN = "_id";
    private static final String LOGS_ENABLE_COLUMN = "logsEnable";
    private static final String PROXY_COLUMN = "proxy";
    private static final String USB_DEVICE_COLUMN = "usb_device";
    private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
            "( " + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
            " " + LOGS_ENABLE_COLUMN + " boolean," +
            " " + PROXY_COLUMN + " varchar(32)," +
            " " + USB_DEVICE_COLUMN + " varchar(32) )";

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
        values.put(ID_COLUMN, 1);
        values.put(LOGS_ENABLE_COLUMN, 1);
        values.put(PROXY_COLUMN, "kornev.pro");
        values.putNull(USB_DEVICE_COLUMN);
        db.insert(TABLE_NAME, null, values);
    }

    public synchronized boolean isLogsEnabled() {
        Cursor cursor = getDatabaseValue(LOGS_ENABLE_COLUMN);
        if (cursor == null) return false;
        boolean result = cursor.getInt(0) == 1;
        cursor.close();
        return result;
    }

    public synchronized void setLogsEnabled(boolean isLogEnabled) {
        ContentValues value = new ContentValues(1);
        value.put(LOGS_ENABLE_COLUMN, isLogEnabled);
        setDatabaseValue(value);
    }

    public String getProxy() {
        Cursor cursor = getDatabaseValue(PROXY_COLUMN);
        if (cursor == null) return null;
        String result = cursor.getString(0);
        cursor.close();
        return result;
    }

    public synchronized void setProxy(String proxy) {
        ContentValues value = new ContentValues(1);
        value.put(PROXY_COLUMN, proxy);
        setDatabaseValue(value);
    }

    public synchronized String getUsbDevice() {
        Cursor cursor = getDatabaseValue(USB_DEVICE_COLUMN);
        if (cursor == null) return null;
        String result = cursor.getString(0);
        cursor.close();
        return result;
    }

    public synchronized void setUsbDevice(String usbDevice) {
        ContentValues value = new ContentValues(1);
        value.put(USB_DEVICE_COLUMN, usbDevice);
        setDatabaseValue(value);
    }

    private Cursor getDatabaseValue(String field) {
        SQLiteDatabase db = getReadableDatabase();
        if (db == null) {
            throw new IllegalStateException("Database " + DB_NAME + " is not exists");
        }
        Cursor cursor = db.query(TABLE_NAME, new String[]{field}, SELECTIONS, SELECTION_ARGS, null, null, ID_COLUMN, LIMIT);
        if ( !cursor.moveToFirst() ) {
            return null;
        }
        return cursor;
    }

    private void setDatabaseValue(ContentValues value) {
        SQLiteDatabase db = getWritableDatabase();
        if (db == null || db.isReadOnly()) {
            throw new IllegalStateException("Database " + DB_NAME + "is can't be write");
        }
        db.update(TABLE_NAME, value, " _id = ?", new String[]{"1"});
        db.close();

    }
}