package com.tennisdc.tln.model;

import android.os.Parcelable;

import org.parceler.Parcel;

import io.realm.RealmObject;

@Parcel
public class DivisionBean  implements Parcelable {
     String name;
     long division_id;
     String division_text = "";

    public static final Creator<DivisionBean> CREATOR = new Creator<DivisionBean>() {
        @Override
        public DivisionBean createFromParcel(android.os.Parcel in) {
            return new DivisionBean(in);
        }

        @Override
        public DivisionBean[] newArray(int size) {
            return new DivisionBean[size];
        }
    };

    public DivisionBean(){

    }

    public String getDivision_text() {
        return division_text;
    }

    public void setDivision_text(String division_text) {
        this.division_text = division_text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDivision_id() {
        return division_id;
    }

    public void setDivision_id(long division_id) {
        this.division_id = division_id;
    }


    protected DivisionBean(android.os.Parcel in) {
        name = in.readString();
        division_id = in.readLong();
        division_text = in.readString();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeLong(division_id);
        parcel.writeString(division_text);
    }
}
