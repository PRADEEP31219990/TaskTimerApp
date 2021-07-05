package com.example.mytasktimerapplication;

import android.util.Log;

import java.io.Serializable;
import java.util.Date;

public class Timing implements Serializable {
    public static final long serialVersionUID = 20161120L;
    private static final String TAG = Timing.class.getSimpleName();
    private long m_Id;
    private Task mTask;
    private long mStartTime;
    private long mDuration;

    public Timing(Task mTask) {
        this.mTask = mTask;
        Date currentTime = new Date();
        mStartTime = currentTime.getTime();
        mDuration = 0;
    }

    public long getId() {
        return m_Id;
    }

    void setId(long id) {
        this.m_Id = id;
    }

    Task getTask() {
        return mTask;
    }

    void setTask(Task Task) {
        this.mTask = Task;
    }

    long getStartTime() {
        return mStartTime;
    }

    void setStartTime(long StartTime) {
        this.mStartTime = StartTime;
    }

    long getDuration() {
        return mDuration;
    }

    void setDuration() {
        Date currentTime = new Date();
        mDuration = (currentTime.getTime()/1000) - mStartTime;
        Log.d(TAG, mTask.getId() + "  Start time " + mStartTime + " Duration " + mDuration);

    }
}
