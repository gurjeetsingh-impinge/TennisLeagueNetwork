package com.tennisdc.tennisleaguenetwork.model;

/**
 * Created on 16-Jul-17.
 */

public class Common {

    public long id;
    public String display_name;

    public Common(String name, long value) {
        this.display_name = name;
        this.id = value;
    }

    @Override
    public String toString() {
        return display_name;
    }
}
