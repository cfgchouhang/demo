package tw.meowdev.cfg.askdemo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by cfg on 5/2/16.
 */
public class Time {
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

    public static String shortStr(String dateStr, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date;
        String shortStr = dateStr;
        try {
            date = sdf.parse(dateStr);
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            shortStr = sdf.format(date);
        } catch (ParseException e){
        }

        return shortStr;
    }
}
