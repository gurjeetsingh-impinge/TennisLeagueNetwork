package com.tennisdc.tennisleaguenetwork.model;

public class SkillLevel {

    public int id;
    public String display_name;

    @Override
    public String toString() {
        return display_name;
    }
}
