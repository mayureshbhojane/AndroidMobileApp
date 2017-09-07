package com.androiddevelopment.mayureshbhojane.todolist.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.androiddevelopment.mayureshbhojane.todolist.mail.Email;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mayuresh Bhojane on 7/7/2017.
 */

/**
 * This class is a helper class to execute database operations.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // class instance to make it a singleton
    private static DatabaseHelper instance = null;

    /*
     * CREATE TABLE emails (
     * _id INTEGER PRIMARY KEY AUTOINCREMENT,
     * emailid TEXT NOT NULL,
     * emailpwd TEXT NOT NULL
     * );
     */
    private static final String createEmailsTable = "CREATE TABLE " + DatabaseConstants.EmailEntry.TABLE + " ( " +
            DatabaseConstants.EmailEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DatabaseConstants.EmailEntry.COL_EMAIL_ID + " TEXT NOT NULL, " +
            DatabaseConstants.EmailEntry.COL_EMAIL_PWD + " TEXT NOT NULL);";

    /*
     * CREATE TABLE tasks (
     * _id INTEGER PRIMARY KEY AUTOINCREMENT,
     * title TEXT NOT NULL
     * );
     */
    private static final String createTasksTable = "CREATE TABLE " + DatabaseConstants.TaskEntry.TABLE + " ( " +
            DatabaseConstants.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DatabaseConstants.TaskEntry.COL_TASK_TITLE + " TEXT NOT NULL);";

    /**
     * This method returns instance of the singleton class.
     * If instance is null, the constructor is called.
     * This method must be called first passing the Context to initialize the class.
     * @param context
     * @return
     */
    public static DatabaseHelper getInstance(Context context) {
        if(instance==null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    /**
     * This method returns instance of the singleton class.
     * This method should not be called before the {@link  #getInstance(Context)}.
     * If the instance is null and this method is called, the method will throw exception
     * @return
     * @throws Exception
     */
    public static DatabaseHelper getInstance() throws Exception {
        if(instance==null) {
            throw new Exception();
        }
        return instance;
    }

    // private constructor
    private DatabaseHelper(Context context) {
        super(context, DatabaseConstants.DB_NAME, null, DatabaseConstants.DB_VERSION);
        // forcing new and empty tables everytime
        onUpgrade(this.getWritableDatabase(), DatabaseConstants.DB_VERSION, DatabaseConstants.DB_VERSION);
    }

    /**
     * This method creates following table in SQLite database:
     * 1. emails(_id, emailid, emailpwd)
     * 2. tasks(_id, title)
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createEmailsTable);
        db.execSQL(createTasksTable);
    }

    /**
     * This method deletes tasks table from SQLite database and then creates it
     * Executes following query statements:
     * DROP TABLE IF EXISTS emails;
     * DROP TABLE IF EXISTS tasks;
     * followed by:
     * call to {@link #onCreate(SQLiteDatabase)}
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.EmailEntry.TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.TaskEntry.TABLE);
        onCreate(db);
    }

    /**
     * This method is a utility method to retrieve all registered emails from database
     * @return list of Email object representing registered emails from database
     */
    public List<Email> getAllEmails() {
        ArrayList<Email> emailList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getWritableDatabase();
            cursor = db.query(DatabaseConstants.EmailEntry.TABLE,
                    new String[]{
                            DatabaseConstants.EmailEntry._ID,
                            DatabaseConstants.EmailEntry.COL_EMAIL_ID,
                            DatabaseConstants.EmailEntry.COL_EMAIL_PWD},
                    null, null, null, null, null);
            while (cursor.moveToNext()) {
                int emailIdx = cursor.getColumnIndex(DatabaseConstants.EmailEntry.COL_EMAIL_ID);
                String id = cursor.getString(emailIdx);
                int emailPwdx = cursor.getColumnIndex(DatabaseConstants.EmailEntry.COL_EMAIL_PWD);
                String pwd = cursor.getString(emailPwdx);
                emailList.add(new Email(id, pwd));
            }
        }
        finally {
            if(cursor!=null) cursor.close();
            if(db!=null) db.close();
        }
        return emailList;
    }

    /**
     * This method is a utility method to retrieve all tasks from database
     * @return list of string containing tasks from database
     */
    public List<String> getAllTasks() {
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getWritableDatabase();
            cursor = db.query(DatabaseConstants.TaskEntry.TABLE,
                    new String[]{DatabaseConstants.TaskEntry._ID, DatabaseConstants.TaskEntry.COL_TASK_TITLE},
                    null, null, null, null, null);
            while (cursor.moveToNext()) {
                int idx = cursor.getColumnIndex(DatabaseConstants.TaskEntry.COL_TASK_TITLE);
                String dbTask = cursor.getString(idx);
                taskList.add(dbTask);
            }
        }
        finally {
            if(cursor!=null) cursor.close();
            if(db!=null) db.close();
        }
        return taskList;
    }

}
