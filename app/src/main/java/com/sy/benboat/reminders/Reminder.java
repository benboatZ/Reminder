package com.sy.benboat.reminders;

/**
 * Created by benboat on 2017/3/8.
 */
public class Reminder {
    private int mId;
    private String mContent;
    private int mImportant;
    private String mTimestamp;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public int getImportant() {
        return mImportant;
    }

    public void setImportant(int important) {
        mImportant = important;
    }

    public Reminder(int id, String content, int important, String timestamp) {
        mId = id;
        mContent = content;
        mImportant = important;
        mTimestamp = timestamp;
    }

    public String getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(String timestamp) {
        mTimestamp = timestamp;
    }
}
