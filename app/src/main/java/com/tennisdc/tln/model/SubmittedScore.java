package com.tennisdc.tln.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created  on 02-02-2015.
 */
@Parcel
public class SubmittedScore {

    @SerializedName("match_result")
    public String MatchResult;

    @SerializedName("date")
    public String MatchDate;

    @SerializedName("score")
    public String Score;

}
