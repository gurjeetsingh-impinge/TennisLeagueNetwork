package com.tennisdc.tennisleaguenetwork.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created on 28-May-17.
 */

public class TournamentRound {

   /* private String TournamentName;
    private String TournamentDate;
    private List<TournamentRoundDetails> TournamentRoundDeatilsList;
    public static final int ItemType = 1000;

    public String getTournamentName() {
        return TournamentName;
    }

    public void setTournamentName(String tournamentName) {
        TournamentName = tournamentName;
    }

    public String getTournamentDate() {
        return TournamentDate;
    }

    public void setTournamentDate(String tournamentDate) {
        TournamentDate = tournamentDate;
    }

    public List<TournamentRoundDetails> getTournamentRoundDeatilsList() {
        return TournamentRoundDeatilsList;
    }

    public void setTournamentRoundDeatilsList(List<TournamentRoundDetails> tournamentRoundDeatilsList) {
        TournamentRoundDeatilsList = tournamentRoundDeatilsList;
    }*/


    @SerializedName("title")
    public String TournamentTitle;

    @SerializedName("date")
    public String TournamentDate;

    @SerializedName("open_default")
    public boolean OpenDefault;

    @SerializedName("matches")
    private List<TournamentRoundDetails> MatchesList;

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
}
