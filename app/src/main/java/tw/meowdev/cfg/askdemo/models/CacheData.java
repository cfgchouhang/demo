package tw.meowdev.cfg.askdemo.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import tw.meowdev.cfg.askdemo.utils.Time;

public class CacheData {
    private static long day = 1 * 24 * 60 * 60 * 1000; // one day in millisecond
    final public static String tableName = "cache";
    final public static String[][] columns = new String[][] {
        {"_id", "INTEGER PRIMARY KEY AUTOINCREMENT"}, 
        {"key", "CHAR"},
        {"json", "CHAR"},
        {"create_time", "CHAR"}
    };

    String key, json, time;

    public CacheData(String key, String json) {
        this.key = key;
        this.json = json;
        this.time = Time.now();
    }

    public void insert(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        for(String[] kv: getColVal()) {
            cv.put(kv[0], kv[1]);
        }
        db.insert(tableName, null, cv);
    }


    
    public String[][] getColVal() {
        return new String[][]{
            {"key", this.key},
            {"json", this.json},
            {"create_time", this.time}
        };
    }

    public static String getData(SQLiteDatabase db, String key, boolean isOnline) {
        Cursor cursor = db.query(tableName, null, "key=?", new String[]{key}, null, null, null, "1");
        String jsonStr = null;

        if(cursor.moveToFirst()) {

            String createTime = cursor.getString(cursor.getColumnIndex("create_time"));
            Log.i("DB", createTime);
            if(Time.timeDiff(createTime, Time.now()) < day || !isOnline)
                jsonStr = cursor.getString(cursor.getColumnIndex("json"));
            else
                db.delete(tableName, "key=?", new String[]{key}); // delete too old cache data
        }
        cursor.close();
        return jsonStr;
    }

    // return local time string

}
