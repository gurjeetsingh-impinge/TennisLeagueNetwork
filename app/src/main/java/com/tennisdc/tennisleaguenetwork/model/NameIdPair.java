package com.tennisdc.tennisleaguenetwork.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created  on 21-01-2015.
 */
@Parcel
public class NameIdPair {

    @SerializedName("name")
    public String Name;

    @SerializedName("id")
    public long Id;

    @SerializedName("non_league")
    public boolean Non_League;

    public NameIdPair(){};

    public NameIdPair(String name, long id, boolean non_League){
        this.Name = name;
        this.Id = id;
        this.Non_League = non_League;
    };

    @Override
    public String toString() {
        return TextUtils.isEmpty(Name) ? super.toString(): Name;
    }

}
