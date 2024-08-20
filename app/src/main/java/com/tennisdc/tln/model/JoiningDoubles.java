package com.tennisdc.tln.model;

/**
 * Created on 29-Jun-17.
 */

public class JoiningDoubles {

    public String display_name;
    public String value;

    public JoiningDoubles(String name, String value) {
        this.display_name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return this.display_name;
    }
}
