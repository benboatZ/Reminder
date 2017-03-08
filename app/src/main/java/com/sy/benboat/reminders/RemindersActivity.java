package com.sy.benboat.reminders;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

public class RemindersActivity extends AppCompatActivity {
    private ListView mListView;
    private RemindersDbAdapter mDbAdapter;
    private ReminderSimpleCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        mListView = (ListView) findViewById(R.id.Reminders_list_view);
       /* ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                R.layout.reminders_row,
                R.id.row_text,
                new String[]{"first record", "second record", "third record"});
        mListView.setAdapter(arrayAdapter);*/
        mListView.setDivider(null);
        mDbAdapter=new RemindersDbAdapter(this);
        mDbAdapter.open();

        Cursor cursor=mDbAdapter.fetchAllReminders();

        String[] from=new String[]{
                RemindersDbAdapter.COL_CONTENT
        };

        int[] to=new int[]{
                R.id.row_text
        };

        mCursorAdapter=new ReminderSimpleCursorAdapter(
                RemindersActivity.this,
                R.layout.reminders_row,
                cursor,
                from,
                to,
                0
        );

        mListView.setAdapter(mCursorAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                Log.d(getLocalClassName(), "Create new Reminder");
                return true;
            case R.id.action_exit:
                finish();
                return true;
            default:
                return false;
        }
    }
}