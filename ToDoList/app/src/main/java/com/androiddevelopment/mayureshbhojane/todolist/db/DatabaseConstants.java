package com.androiddevelopment.mayureshbhojane.todolist.db;

import android.provider.BaseColumns;

/**
 * Created by Mayuresh Bhojane on 7/7/2017.
 */

/**
 * This class contains all database constants.
 */
public class DatabaseConstants {

    public static final String DB_NAME = "com.androiddevelopment.mayureshbhojane.todolist.db";
    public static final int DB_VERSION = 1;

    public class EmailEntry implements BaseColumns {
        public static final String TABLE = "emails";
        public static final String COL_EMAIL_ID = "emailid";
        public static final String COL_EMAIL_PWD = "emailpwd";
    }

    public class TaskEntry implements BaseColumns {
        public static final String TABLE = "tasks";
        public static final String COL_TASK_TITLE = "title";
    }

}
