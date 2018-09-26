package com.example.mohamedramadan.mychat;

import android.app.Application;
import android.content.Context;

public class LastSeen extends Application {
    private static final int SECOND_MILLES = 1000;
    private static final int MINUTE_MILLES = 60 * SECOND_MILLES;
    private static final int HOUR_MILLES = 60 * MINUTE_MILLES;
    private static final int DAY_MILLES = 60 * HOUR_MILLES;

    public static String getTimeApp(long time)
    {
        if(time < 1000000000000L)
        {
            time *= 1000;
        }
        long now = System.currentTimeMillis();
        if(time > now || time <= 0)
        {
            return null;
        }
        final long diff = now - time;
        if(diff < MINUTE_MILLES)
        {
            return "just now";
        }else if (diff < 2 * MINUTE_MILLES)
        {
            return "a minute ago";
        }else if (diff < 50 * MINUTE_MILLES)
        {
            return diff / MINUTE_MILLES + " minute ago";
        }else if (diff < 90 * MINUTE_MILLES)
        {
                return "an hour ago";
        }else if (diff < 24 * HOUR_MILLES)
        {
            return diff / HOUR_MILLES + " hours ago";
        }else if (diff < 48 * HOUR_MILLES)
        {
            return "Yesterday";
        }else{
            int day = (int) ((diff / DAY_MILLES) + 1);
            return day + " days ago";
        }
    }
}
