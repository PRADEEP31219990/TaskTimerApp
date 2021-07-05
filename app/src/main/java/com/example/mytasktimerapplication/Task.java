package com.example.mytasktimerapplication;

import java.io.Serializable;

public class Task implements Serializable {
    public static final long serialVersionUID = 20161120L;
    private long id;
    private final String mName;
    private final String mDescription;
    private final int mSortOrder;

    Task(long id, String mName, String mDescription, int mSortOrder) {
        this.id = id;
        this.mName = mName;
        this.mDescription = mDescription;
        this.mSortOrder = mSortOrder;
    }

    long getId() {
        return id;
    }

    String getmName() {
        return mName;
    }

    String getmDescription() {
        return mDescription;
    }

    int getmSortOrder() {
        return mSortOrder;
    }

    void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", mName='" + mName + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mSortOrder=" + mSortOrder +
                '}';
    }
}
