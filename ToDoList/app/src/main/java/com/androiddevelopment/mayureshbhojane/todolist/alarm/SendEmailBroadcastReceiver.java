package com.androiddevelopment.mayureshbhojane.todolist.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.androiddevelopment.mayureshbhojane.todolist.mail.SendEmailTask;

/**
 * Created by Mayuresh Bhojane on 8/17/2017.
 */

/**
 * This class extends BroadcastReceiver that receives and handles broadcast intents
 * for sending email with all database tasks to registered emails
 */
public class SendEmailBroadcastReceiver extends BroadcastReceiver {

    public static final String TASK_LIST = "TASKLIST";

    /**
     * This method is called by the BroadcastReceiver workflow
     * and calls SendEmailTask class to execute sending email task
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TAG");
        // Acquire the lock
        wl.acquire();
        // execute sending email task
        new SendEmailTask().execute();
        // Release the lock
        wl.release();
    }
}
