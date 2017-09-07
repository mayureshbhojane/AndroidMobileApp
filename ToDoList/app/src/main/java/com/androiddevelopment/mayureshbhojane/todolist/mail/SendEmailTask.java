package com.androiddevelopment.mayureshbhojane.todolist.mail;

import android.os.AsyncTask;

import com.androiddevelopment.mayureshbhojane.todolist.db.DatabaseHelper;
import com.androiddevelopment.mayureshbhojane.todolist.utils.AESCrypt;

/**
 * Created by Mayuresh Bhojane on 8/16/2017.
 */

/**
 * This class will build body of the email from saved tasks in database,
 * retrieve all saved tasks and registered emails from database
 * and call corresponding email classes to perform actual email sending task
 */
public class SendEmailTask extends AsyncTask<String, Void, String> {

    /**
     * This method builds body of the email from saved tasks in database,
     * loops over each registered email after retrieving from database
     * and calls corresponding email class to perform actual email sending task
     * @param strings
     * @return
     */
    @Override
    protected String doInBackground(String... strings) {
        String toReturn;
        try {
            // retrieve all registered email from database
            DatabaseHelper dbHelper = DatabaseHelper.getInstance();
            // get all tasks from database and build the email body from it
            String emailBody = "";
            for (String dbTask : dbHelper.getAllTasks()) {
                emailBody = emailBody + dbTask + "\n";
            }
            // send email only if there are saved tasks
            if(emailBody!=null && emailBody.length()>0) {
                for (Email email : dbHelper.getAllEmails()) {
                    String emailId = email.getId();
                    String encrEmailPwd = email.getPwd();
                    String emailPwd = null;
                    try {
                        emailPwd = AESCrypt.decrypt(encrEmailPwd);
                    }
                    catch (Exception e) {
                        emailPwd = encrEmailPwd;
                    }
                    // gmail
                    if (emailId!=null && emailId.endsWith(GmailSender.GMAIL)) {
                        GmailSender gmail = new GmailSender(emailId, emailPwd);
                        gmail.sendMail(emailBody);
                    }
                    else if(emailId!=null &&
                            (emailId.endsWith(YahooSender.YAHOO_COM) || emailId.endsWith(YahooSender.YAHOO_CO_IN))) {
                        YahooSender yahoo = new YahooSender(emailId, emailPwd);
                        yahoo.sendMail(emailBody);
                    }
                }
            }
            toReturn = "email send";
        }
        catch (Exception e) {
            toReturn = "email sending failed";
        }
        return toReturn;
    }

}
