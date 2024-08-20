package com.tennisdc.tln.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created on 28-May-17.
 */

public class TournamentRound implements Parcelable {

    public static final int ItemType = 1000;
    public static final Creator<TournamentRound> CREATOR = new Creator<TournamentRound>() {
        @Override
        public TournamentRound createFromParcel(Parcel in) {
            return new TournamentRound(in);
        }

        @Override
        public TournamentRound[] newArray(int size) {
            return new TournamentRound[size];
        }
    };
    @SerializedName("title")
    public String TournamentTitle;
    @SerializedName("date")
    public String TournamentDate;
    @SerializedName("open_default")
    public boolean OpenDefault;
    private String TournamentName;
    //    private String TournamentDate;
    private List<TournamentRoundDetails> TournamentRoundDeatilsList;
    @SerializedName("matches")
    private List<TournamentRoundDetails> MatchesList;

//    public String getTournamentDate() {
//        return TournamentDate;
//    }
//
//    public void setTournamentDate(String tournamentDate) {
//        TournamentDate = tournamentDate;
//    }

    protected TournamentRound(Parcel in) {
        TournamentName = in.readString();
        TournamentTitle = in.readString();
        TournamentDate = in.readString();
        OpenDefault = in.readByte() != 0;
    }

    public TournamentRound() {
    }

    public String getTournamentName() {
        return TournamentName;
    }

    public void setTournamentName(String tournamentName) {
        TournamentName = tournamentName;
    }

    public List<TournamentRoundDetails> getTournamentRoundDeatilsList() {
        return TournamentRoundDeatilsList;
    }

    public void setTournamentRoundDeatilsList(List<TournamentRoundDetails> tournamentRoundDeatilsList) {
        TournamentRoundDeatilsList = tournamentRoundDeatilsList;
    }

    public String getTournamentTitle() {
        return TournamentTitle;
    }

    public void setTournamentTitle(String tournamentTitle) {
        TournamentTitle = tournamentTitle;
    }

    public String getTournamentDate() {
        return TournamentDate;
    }

    public void setTournamentDate(String tournamentDate) {
        TournamentDate = tournamentDate;
    }

    public boolean isOpenDefault() {
        return OpenDefault;
    }

    public void setOpenDefault(boolean openDefault) {
        OpenDefault = openDefault;
    }

    public List<TournamentRoundDetails> getMatchesList() {
        return MatchesList;
    }

    public void setMatchesList(List<TournamentRoundDetails> matchesList) {
        MatchesList = matchesList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(TournamentName);
        parcel.writeString(TournamentTitle);
        parcel.writeString(TournamentDate);
        parcel.writeByte((byte) (OpenDefault ? 1 : 0));
    }
}
