package com.tennisdc.tln.model;

import io.realm.RealmList;
import com.tennisdc.tennisleaguenetwork.model.Court;

public class CourtMainModel {

    private RealmList<Court> courts_list;
    private double parent_lat;
    private double parent_long;

    public RealmList<Court> getCourts_list() {
        return courts_list;
    }

    public void setCourts_list(RealmList<Court> courts_list) {
        this.courts_list = courts_list;
    }

    public double getParent_lat() {
        return parent_lat;
    }

    public void setParent_lat(double parent_lat) {
        this.parent_lat = parent_lat;
    }

    public double getParent_long() {
        return parent_long;
    }

    public void setParent_long(double parent_long) {
        this.parent_long = parent_long;
    }
}
