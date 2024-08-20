package com.tennisdc.tennisleaguenetwork.model;

/**
 * Created by SSS on 16-Apr-2018.
 */

public class ReferFriendEmailAddress {

    public long id;
    public String title;
    public String emailAddr;

    public ReferFriendEmailAddress(long id, String title, String emailAddr) {
        this.emailAddr = emailAddr;
        this.title = title;
        this.id = id;
    }

    public String getEmailAddr() {
        return emailAddr;
    }

    public void setEmailAddr(String emailAddr) {
        this.emailAddr = emailAddr;
    }
}
