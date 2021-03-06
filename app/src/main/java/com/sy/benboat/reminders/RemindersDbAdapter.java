package com.sy.benboat.reminders;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by benboat on 2017/3/8.
 */
public class RemindersDbAdapter {
//    these are the column names
    public static final String COL_ID = "_id";
    public static final String COL_CONTENT = "content";
    public static final String COL_IMPORTANT = "important";
    public static final String COL_TIMESTAMP = "timestamp";
    public static final String COL_CONTENTMORE = "contentmore";

    public static final int INDEX_ID=0;
    public static final int INDEX_CONTENT=INDEX_ID+1;
    public static final int INDEX_IMPORTANT=INDEX_ID+2;
    public static final int INDEX_TIMESTAMP=INDEX_ID+3;

    public static final String TAG="RemindersDbAdapter";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    public static final String DATABASE_NAME="dba_remdrs";
    public static final String TABLE_NAME="tbl_remdrs";
    public static final int DATABASE_VERSION =2;

    private final Context mCtx;

    public static final String DATABASE_CREATE=
            "CREATE TABLE if not exists "+TABLE_NAME+"( "+
                    COL_ID+" INTEGER PRIMARY KEY autoincrement, "+
                    COL_CONTENT+" TEXT, "+
                    COL_IMPORTANT+" INTEGER, "+
    COL_TIMESTAMP +" DATETIME DEFAULT CURRENT_TIMESTAMP);";

    public RemindersDbAdapter(Context ctx) {
        mCtx = ctx;
    }

    public void open() throws SQLException{
        mDbHelper=new DatabaseHelper(mCtx);
        mDb=mDbHelper.getWritableDatabase();
    }

    public void  close()
    {
        if(mDbHelper!=null){
            mDbHelper.close();
        }
    }

    public void createReminder(String name,boolean important){
        ContentValues values=new ContentValues();
        values.put(COL_CONTENT,name);
        values.put(COL_IMPORTANT,important?1:0);
        values.put(COL_TIMESTAMP,getNowFormatDate() );
        mDb.insert(TABLE_NAME,null,values);
    }

    public long createReminder(Reminder reminder){
        ContentValues values=new ContentValues();
        values.put(COL_CONTENT,reminder.getContent());
        values.put(COL_IMPORTANT,reminder.getImportant());
        values.put(COL_TIMESTAMP,getNowFormatDate() );
       return mDb.insert(TABLE_NAME,null,values);
    }

    public Reminder fetchReminderById(int id) {
        Cursor cursor;
        cursor = mDb.query(TABLE_NAME, new String[]{COL_ID,
                        COL_CONTENT, COL_IMPORTANT,COL_TIMESTAMP}, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null
        );
        if (cursor != null)
            cursor.moveToFirst();

        return new Reminder(
                cursor.getInt(INDEX_ID),
                cursor.getString(INDEX_CONTENT),
                cursor.getInt(INDEX_IMPORTANT),
                cursor.getString(INDEX_TIMESTAMP)
        );
    }

    public Cursor fetchAllReminders(){
        Cursor mCursor=mDb.query(TABLE_NAME,new String[]{COL_ID,
                COL_CONTENT, COL_IMPORTANT,COL_TIMESTAMP,"(content||'('||timestamp||')') as "+COL_CONTENTMORE},null,null,null,null,null
        );

        if(mCursor!=null)
            mCursor.moveToFirst();


        return mCursor;
    }

    public void updateReminder(Reminder reminder){
        ContentValues values=new ContentValues();
        values.put(COL_CONTENT,reminder.getContent());
        values.put(COL_IMPORTANT,reminder.getImportant());

        values.put(COL_TIMESTAMP,getNowFormatDate() );
        mDb.update(TABLE_NAME,values,COL_ID+"=?",
                new String[]{String.valueOf(reminder.getId())});

    }

    private String getNowFormatDate() {
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }

    public void deleteReminder(int nId){
        mDb.delete(TABLE_NAME,COL_ID+"=?",
                new String[]{String.valueOf(nId)});
    }

    public void deleteAllReminder(){
        mDb.delete(TABLE_NAME,null,null);
    }

    private  static  class  DatabaseHelper extends SQLiteOpenHelper{
        public DatabaseHelper(Context context) {
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG,DATABASE_CREATE );
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "upgrading database from version "+oldVersion+" to "
                    +newVersion+",which will destroy all old data" );
            db.execSQL("drop table if exists "+TABLE_NAME);
            onCreate(db);


        }
    }



}
