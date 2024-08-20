package com.tennisdc.tennisleaguenetwork.model;

public class UstaRank {

    public String display_name;
    public String value;

    public UstaRank(String name, String value) {
        this.display_name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return this.display_name;
    }
}
