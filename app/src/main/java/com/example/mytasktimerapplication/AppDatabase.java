package com.example.mytasktimerapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.LongDef;

class AppDatabase extends SQLiteOpenHelper {
    private static final String TAG = "AppDatabase";
    public static final String DATABASE_NAME = "TaskTimer.db";
    public static final int DATABASE_VERSION = 2;

    // Implement AppDatabase as a Singleton
    private static AppDatabase instance = null;

    private AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "AppDatabase: constructor");
    }


    static AppDatabase getInstance(Context context) {
        if(instance == null) {
            Log.d(TAG, "getInstance: creating new instance");
            instance = new AppDatabase(context);
        }
        return instance;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: starts");
        String sSQL;    // Use a string variable to facilitate logging
//        sSQL = "CREATE TABLE Tasks (_id INTEGER PRIMARY KEY NOT NULL, Name TEXT NOT NULL, Description TEXT, SortOrder INTEGER, CategoryID INTEGER);";
        sSQL = "CREATE TABLE " + TasksContract.TABLE_NAME + " ("
                + TasksContract.columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + TasksContract.columns.TASK_NAME + " TEXT NOT NULL, "
                + TasksContract.columns.TASKS_DESCRIPTION + " TEXT, "
                + TasksContract.columns.TASKS_SORTORDER + " INTEGER);";
        Log.d(TAG, sSQL);
        db.execSQL(sSQL);

        addTimingTable(db);
        addDurationsView(db);

        Log.d(TAG, "onCreate: ends");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: starts");
        switch(oldVersion) {
            case 1:
                addTimingTable(db);
                addDurationsView(db);
                break;
            default:
                throw new IllegalStateException("onUpgrade() with unknown newVersion: " + newVersion);
        }
        Log.d(TAG, "onUpgrade: ends");
    }

    private void addTimingTable(SQLiteDatabase db){
        String sSQL = "CREATE TABLE " + TimingsContract.TABLE_NAME + " ("
                + TimingsContract.columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + TimingsContract.columns.TIMINGS_TASK_ID + " INTEGER NOT NULL, "
                + TimingsContract.columns.TIMINGS_START_TIME + " INTEGER, "
                + TimingsContract.columns.TIMINGS_DURATION + " INTEGER);";
        Log.d(TAG, sSQL);
        db.execSQL(sSQL);

        sSQL = "CREATE TRIGGER Remove_Task"
                + " AFTER DELETE ON " + TasksContract.TABLE_NAME
                + " FOR EACH ROW"
                + " BEGIN"
                + " DELETE FROM " + TimingsContract.TABLE_NAME
                + " WHERE " + TimingsContract.columns.TIMINGS_TASK_ID + " = OLD." + TimingsContract.columns._ID + ";"
                + " END;";
        Log.d(TAG, sSQL);
        db.execSQL(sSQL);
    }

    private void addDurationsView(SQLiteDatabase db){
        String sSQL = "CREATE VIEW " + DurationsContract.TABLE_NAME
                + " AS SELECT " + TimingsContract.TABLE_NAME + "." + TimingsContract.columns._ID + ", "
                + TasksContract.TABLE_NAME + "." + TasksContract.columns.TASK_NAME + ", "
                + TasksContract.TABLE_NAME + "." + TasksContract.columns.TASKS_DESCRIPTION + ", "
                + TimingsContract.TABLE_NAME + "." + TimingsContract.columns.TIMINGS_START_TIME + ", "
                + " DATE(" + TimingsContract.TABLE_NAME + "."  + TimingsContract.columns.TIMINGS_START_TIME + ", 'unixepoch')"
                + " AS " + DurationsContract.columns.DURATIONS_START_DATE + ","
                + " SUM(" + TimingsContract.TABLE_NAME  + "." + TimingsContract.columns.TIMINGS_DURATION + ")"
                + " AS " + DurationsContract.columns.DURATIONS_DURATIONS
                + " FROM " + TasksContract.TABLE_NAME + " JOIN " + TimingsContract.TABLE_NAME
                + " ON " + TasksContract.TABLE_NAME + "." + TasksContract.columns._ID + " = "
                + TimingsContract.TABLE_NAME + "." + TimingsContract.columns.TIMINGS_TASK_ID
                + " GROUP BY " + DurationsContract.columns.DURATIONS_START_DATE + ", " + DurationsContract.columns.DURATIONS_NAME
                + ";";
        Log.d(TAG, sSQL);
        db.execSQL(sSQL);
    }
}
