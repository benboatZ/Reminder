package com.sy.benboat.reminders;

/**
 * Created by benboat on 2017/3/8.
 */
public class Reminder {
    private int mId;
    private String mContent;
    private int mImportant;

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

    public Reminder(int id, String content, int important) {

        mId = id;
        mContent = content;
        mImportant = important;
    }
}
