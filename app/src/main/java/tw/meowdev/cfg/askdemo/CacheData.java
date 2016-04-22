package tw.meowdev.cfg.askdemo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


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
        this.time = now();
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

    public static String getData(SQLiteDatabase db, String key) {
        Cursor cursor = db.query(tableName, null, "key=?", new String[]{key}, null, null, null, "1");
        String jsonStr = null;

        if(cursor.moveToFirst()) {
            String createTime = cursor.getString(cursor.getColumnIndex("create_time"));
            if(timeDiff(createTime, now()) < day)
                jsonStr = cursor.getString(cursor.getColumnIndex("json"));
            else
                db.delete(tableName, "key=?", new String[]{key}); // delete too old cache data
        }
        cursor.close();
        return jsonStr;
    }

    // return local time string
    public static String now() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return sdf.format(cal.getTime());
    }

    public static long timeDiff(String from, String to) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long diff = 0L;
        try {
            Date d1 = sdf.parse(to), d2 = sdf.parse(from);
            diff = d1.getTime()-d2.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return diff;

    }
}
