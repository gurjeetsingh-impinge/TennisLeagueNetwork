package com.tennisdc.tennisleaguenetwork.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created  on 2015-03-10.
 */
@Parcel
public class ChallengeIncoming {

    @SerializedName("offer_header")
    public String Header;

    @SerializedName("offer_message")
    public String Message;

    @SerializedName("accept_button")
    public boolean IsAcceptButtonVisible;

    @SerializedName("player_id")
    public long FromPlayerId;   //Id from which request received. Send with accept and decline

}
