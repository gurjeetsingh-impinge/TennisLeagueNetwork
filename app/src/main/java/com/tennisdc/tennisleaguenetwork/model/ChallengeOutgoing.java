package com.tennisdc.tennisleaguenetwork.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created  on 2015-03-10.
 */
@Parcel
public class ChallengeOutgoing {

    @SerializedName("challenge_header")
    public String Header;

    @SerializedName("challenge_message")
    public String Message;

    @SerializedName("opponent_id")
    public long OpponentId; //send on cancel request

    @SerializedName("status")
    public String Status; //: null|”accepted”|“waiting”

}
