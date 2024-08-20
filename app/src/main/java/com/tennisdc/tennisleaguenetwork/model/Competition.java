package com.tennisdc.tennisleaguenetwork.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created  on 02-02-2015.
 */
@Parcel
public class Competition {

    @SerializedName("competition_name")
    public String CompetitionName;

    @SerializedName("competition_id")
    public long CompetitionId;

    @SerializedName("division_id")
    public long DivisionId;

    @SerializedName("division_name")
    public String DivisionName;

    public CompetitionType Type;

    @Override
    public String toString() {
        return CompetitionName;
    }
}
