package com.androiddevelopment.mayureshbhojane.todolist.mail;

/**
 * Created by Mayuresh Bhojane on 8/30/2017.
 */

public class Email {

    private String id, pwd;

    public Email(String id, String pwd) {
        this.id = id;
        this.pwd = pwd;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

}
