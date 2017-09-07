package com.androiddevelopment.mayureshbhojane.todolist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.androiddevelopment.mayureshbhojane.todolist.alarm.SendEmailBroadcastReceiver;
import com.androiddevelopment.mayureshbhojane.todolist.db.DatabaseConstants;
import com.androiddevelopment.mayureshbhojane.todolist.db.DatabaseHelper;
import com.androiddevelopment.mayureshbhojane.todolist.utils.AESCrypt;
import com.androiddevelopment.mayureshbhojane.todolist.utils.AsteriskPasswordTransformationMethod;
import com.androiddevelopment.mayureshbhojane.todolist.utils.ClearPasswordTransformationMethod;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * This is the Java source class for application's primary activity.
 */
public class MainActivity extends AppCompatActivity {

    // helper class for db operations
    private DatabaseHelper dbHelper;
    // view group to display list of tasks
    private ListView taskListView;
    // spinner view to provide other options for a single task
    private Spinner spinner;
    // adapter to populate tasks from db in the ListView
    private ArrayAdapter<String> mAdapter;
    // instance of class extending BroadcastReceiver
    private SendEmailBroadcastReceiver sendEmailBroadcastReceiver;

    /**
     * This method is called when the activity is being created for first time.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize DatabaseHelper class
        dbHelper = DatabaseHelper.getInstance(this);
        // get ListView from activity_main.xml
        taskListView = (ListView) findViewById(R.id.listView_tasks);
        // initialize email broadcast receiver and register alarm to send email
        sendEmailBroadcastReceiver = new SendEmailBroadcastReceiver();
        registerEmailAlarm();

        /**
         * Following code was an attempt to add spinner view.
         * The aim was to provide app user other options for task entry like:
         * 1. Edit : edit existing task
         * 2. Remind : add reminder for a task
         * The spinner is visible on the UI and the options are also being populated.
         * But all attempts to execute workflow on item selection failed.
         * {@link SpinnerTaskOntemSelectedListener#onItemSelected(AdapterView, View, int, long)}
         * does not get called when an item in the spinnner view is selected.
         **/
        /**
        spinner = (Spinner) getLayoutInflater().inflate(R.layout.item_todo, null).
                findViewById(R.id.spinner_task);
        ArrayAdapter spinnerAdapter = ArrayAdapter.createFromResource
                (getLayoutInflater().inflate(R.layout.item_todo, null).getContext(),
                        R.array.listOption,
                        android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new SpinnerTaskOntemSelectedListener());
        **/
        // update view with updated task list
        updateUI();
    }

    /**
     * This method registers SendEmailBroadcastReceiver class
     * to execute as alarm at:
     * 1. once every day at 6:00 AM for production
     * 2. repeating after every 10 seconds for testing purposes
     */
    private void registerEmailAlarm() {
        Context context = getApplicationContext();
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SendEmailBroadcastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        // once every day at 6:00 AM
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
        // repeating after every 10 seconds
        // am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 10 , pi);
    }

    /**
     * This method updates the ListView with latest task entries from db.
     */
    private void updateUI() {
        // add all the task entries from db to array list
        ArrayList<String> taskList = new ArrayList<>();
        taskList.addAll(dbHelper.getAllTasks());
        // check if adapter is already initialized
        if (mAdapter == null) {
            // if not, initialize and add task entries
            mAdapter = new ArrayAdapter<>(this,
                    R.layout.item_todo,
                    R.id.textView_task,
                    taskList);
            taskListView.setAdapter(mAdapter);
        } else {
            // if initialized, clear and add task entries
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * This method inflates the menu item in the main activity.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This method performs various actions as per selected menu item.
     * Currently, it only supports following menu items:
     * 1. add new task
     * 2. register email
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // add new task
            case R.id.item_addNewTask:
                final EditText taskEditText = new EditText(this);
                // pop up a dialog box to add new task
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Add a new task")
                        .setMessage("What do you want to do next?")
                        .setView(taskEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            // on click of add button, add task entry to db
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String task = String.valueOf(taskEditText.getText());
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put(DatabaseConstants.TaskEntry.COL_TASK_TITLE, task);
                                db.insertWithOnConflict(DatabaseConstants.TaskEntry.TABLE,
                                        null,
                                        values,
                                        SQLiteDatabase.CONFLICT_REPLACE);
                                db.close();
                                updateUI();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
                return true;
            // register email
            case R.id.item_registerEmail:
                // linear layout to populate 2 text boxes
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);
                // email text box
                final EditText emailText = new EditText(this);
                emailText.setHint("Email");
                layout.addView(emailText);
                // password text box
                final EditText passwordText = new EditText(this);
                passwordText.setHint("Password");
                passwordText.setTransformationMethod(new AsteriskPasswordTransformationMethod());
                layout.addView(passwordText);
                // password toggle hide/unhide checkbox
                final CheckBox checkBox = new CheckBox(this);
                checkBox.setHint("Show password");
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                        if(isChecked) {
                            // display original password text
                            passwordText.setTransformationMethod(new ClearPasswordTransformationMethod());
                        }
                        else {
                            // display asterisk for password text
                            passwordText.setTransformationMethod(new AsteriskPasswordTransformationMethod());
                        }
                    }
                });
                layout.addView(checkBox);
                // pop up a dialog box to add new email and password
                AlertDialog emailDialog = new AlertDialog.Builder(this)
                        .setTitle("Add an email")
                        .setView(layout)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            // on click of add button, add task entry to db
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String email = String.valueOf(emailText.getText());
                                String password = String.valueOf(passwordText.getText());
                                String encrPassword = null;
                                try {
                                    encrPassword = AESCrypt.encrypt(password);
                                }
                                catch (Exception e) {
                                    encrPassword = password;
                                }
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put(DatabaseConstants.EmailEntry.COL_EMAIL_ID, email);
                                values.put(DatabaseConstants.EmailEntry.COL_EMAIL_PWD, encrPassword);
                                db.insertWithOnConflict(DatabaseConstants.EmailEntry.TABLE,
                                        null,
                                        values,
                                        SQLiteDatabase.CONFLICT_REPLACE);
                                db.close();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                emailDialog.show();
                return true;
            // default
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This method is executed on "Completed" button click.
     * @param view
     */
    public void completedTask(View view) {
        View parent = (View) view.getParent();
        final TextView taskTextView = (TextView) parent.findViewById(R.id.textView_task);
        // pop up a dialog box to confirm deletion task
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Are you sure you want to delete this task?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    // on click of delete button, delete task entry from db
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(taskTextView.getText());
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        db.delete(DatabaseConstants.TaskEntry.TABLE,
                                DatabaseConstants.TaskEntry.COL_TASK_TITLE + " = ?",
                                new String[]{task});
                        db.close();
                        updateUI();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    /**
     * This method is executed on "Edit" button click.
     * @param view
     */
    public void editTask(View view) {
        View parent = (View) view.getParent();
        final TextView oldTextView = (TextView) parent.findViewById(R.id.textView_task);
        final EditText newEditText = new EditText(this);
        newEditText.setText(oldTextView.getText());
        // pop up a dialog box to edit task
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Edit task")
                .setView(newEditText)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    // on click of OK button, update task entry in db
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int dbtaskId = 0;
                        String task = String.valueOf(newEditText.getText());
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        // get the ID of old task
                        Cursor cursor = db.query(DatabaseConstants.TaskEntry.TABLE,
                                new String[]{DatabaseConstants.TaskEntry._ID, DatabaseConstants.TaskEntry.COL_TASK_TITLE},
                                null, null, null, null, null);
                        while (cursor.moveToNext()) {
                            int idx = cursor.getColumnIndex(DatabaseConstants.TaskEntry.COL_TASK_TITLE);
                            String dbTask = cursor.getString(idx);
                            if(oldTextView.getText()!=null && oldTextView.getText().equals(dbTask)) {
                                dbtaskId = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.TaskEntry._ID));
                                break;
                            }
                        }
                        // update the old task with new task using ID
                        ContentValues cv = new ContentValues();
                        cv.put(DatabaseConstants.TaskEntry.COL_TASK_TITLE, task);
                        db.updateWithOnConflict(DatabaseConstants.TaskEntry.TABLE,
                                cv,
                                DatabaseConstants.TaskEntry._ID + " = " + dbtaskId,
                                null,
                                SQLiteDatabase.CONFLICT_FAIL);
                        db.close();
                        updateUI();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

}
