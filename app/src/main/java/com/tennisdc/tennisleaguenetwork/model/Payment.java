package com.tennisdc.tennisleaguenetwork.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created  on 2015-05-05.
 */
public class Payment extends RealmObject {

    @PrimaryKey
    private String id;

    private String jsonString;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }
}
