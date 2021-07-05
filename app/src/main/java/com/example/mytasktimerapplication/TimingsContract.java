package com.example.mytasktimerapplication;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.example.mytasktimerapplication.AppProvider.CONTENT_AUTHORITY;
import static com.example.mytasktimerapplication.AppProvider.CONTENT_AUTHORITY_URI;

public class TimingsContract {
    static final String TABLE_NAME = "Timings";
    public static class columns{
        public static final String _ID = BaseColumns._ID;
        public static final String TIMINGS_TASK_ID = "TaskId";
        public static final String TIMINGS_START_TIME = "StartTime";
        public static final String TIMINGS_DURATION = "Duration";
        public columns(){

        }
    }
    public static final Uri CONTENT_URI = Uri.withAppendedPath(CONTENT_AUTHORITY_URI,TABLE_NAME);
    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + "." + TABLE_NAME;
    static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd" + CONTENT_AUTHORITY + "." + TABLE_NAME;

    public static Uri buildTimingUri(long TimingId){
        return ContentUris.withAppendedId(CONTENT_URI,TimingId);
    }
    public static long getTimingId(Uri uri){
        return ContentUris.parseId(uri);
    }
}


