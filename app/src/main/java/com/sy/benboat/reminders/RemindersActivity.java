package com.sy.benboat.reminders;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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

        if(savedInstanceState==null){
            //Clear all data
            mDbAdapter.deleteAllReminder();
            //Add some data
            insertSomeReminders();
    }

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

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int masterListPostion, long id) {
//                Toast.makeText(RemindersActivity.this,"clicked"+position,Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder=new AlertDialog.Builder(RemindersActivity.this);
                ListView modelListView=new ListView(RemindersActivity.this);
                String[] modes=new String[]{"Edit Reminder","Delete Reminder"};
                ArrayAdapter<String> modeAdapter=new
                        ArrayAdapter<String>(RemindersActivity.this,
                        android.R.layout.simple_list_item_1,android.R.id.text1,modes);
                modelListView.setAdapter(modeAdapter);
                builder.setView(modelListView);
                final Dialog dialog=builder.create();
                dialog.show();
                modelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //edit reminder
                        if(position==0){
//                            Toast.makeText(RemindersActivity.this,"edit "
//                            + masterListPostion,Toast.LENGTH_SHORT).show();
                            int nId=getIdFromPosition(masterListPostion);
                            Reminder reminder=mDbAdapter.fetchReminderById(nId);
                            fireCustomDialog(reminder);
                        }
                        else{
                            //delete reminder
//                            Toast.makeText(RemindersActivity.this,"delete "
//                                    + masterListPostion,Toast.LENGTH_SHORT).show();
                            mDbAdapter.deleteReminder(getIdFromPosition(masterListPostion));
                            mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                        }
                        dialog.dismiss();
                    }
                });

            }
        })  ;

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB){
            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater =mode.getMenuInflater();
                    inflater.inflate(R.menu.can_menu,menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                   switch (item.getItemId()){
                       case R.id.menu_item_delete_reminder:
                           for(int nC=mCursorAdapter.getCount()-1;nC>=0;nC--){
                               if(mListView.isItemChecked(nC)){
                                   mDbAdapter.deleteReminder(getIdFromPosition(nC));
                               }
                           }
                           mode.finish();
                           mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                           return true;
                   }
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
        }
    }

    private int getIdFromPosition(int nC) {
        return (int)mCursorAdapter.getItemId(nC);
    }

    private void insertSomeReminders() {
        mDbAdapter.createReminder("沪江英语课件课",true);
        mDbAdapter.createReminder("沪江英语口语课预习",true);
        mDbAdapter.createReminder("新加坡邮件回复",false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                Log.d(getLocalClassName(), "Create new Reminder");
                //create new Reminder
                fireCustomDialog(rnull);
                return true;
            case R.id.action_exit:
                finish();
                return true;
            default:
                return false;
        }
    }

    private void fireCustomDialog(final Reminder reminder){
        //custom dailog
        final Dialog dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom);

        TextView titleView= (TextView) dialog.findViewById(R.id.custom_title);
        final EditText editCustom= (EditText) dialog.findViewById(R.id.custom_edit_reminder);
        final CheckBox checkbox= (CheckBox) dialog.findViewById(R.id.custom_check_box);

        Button buttionCommit= (Button) dialog.findViewById(R.id.custom_button_commit);
        LinearLayout rootLayout= (LinearLayout) dialog.findViewById(R.id.custom_root_layout);

        final boolean isEditOPeration=(reminder!=null);

        //this is for an edit
        if(isEditOPeration){
            titleView.setText("Edit Reminder");
            checkbox.setChecked(reminder.getImportant()==1);
            editCustom.setText(reminder.getContent());
            rootLayout.setBackgroundColor(getResources().getColor(R.color.blue));
        }

        buttionCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reminderText=editCustom.getText().toString();
                if(isEditOPeration){
                    Reminder reminderEdited=new Reminder(reminder.getId(),
                            reminderText,checkbox.isChecked()?0:1);
                    mDbAdapter.updateReminder(reminderEdited);
                    //this is for new reminder
                }else{
                    mDbAdapter.createReminder(reminderText, checkbox.isChecked());
                }
                mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                dialog.dismiss();
            }
        });
        Button buttonCancel= (Button) dialog.findViewById(R.id.custom_button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}