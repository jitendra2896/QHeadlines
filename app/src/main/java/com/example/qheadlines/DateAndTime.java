package com.example.qheadlines;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateAndTime {
    private static String format = "dd/MM/yy";

    public static String getFormattedDate(long milliSeconds){
        DateFormat dateFormat = new SimpleDateFormat(format);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return dateFormat.format(calendar.getTime());
    }

    public static String getDate(long time){
        return getFormattedDate(time);
    }

    public static void setFormat(String format){
        DateAndTime.format = format;
    }
}
