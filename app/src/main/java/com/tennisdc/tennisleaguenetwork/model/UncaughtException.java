package com.tennisdc.tennisleaguenetwork.model;

import io.realm.RealmObject;

/**
 * Created  on 17-03-2015.
 */
public class UncaughtException extends RealmObject {

    private String details;

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
