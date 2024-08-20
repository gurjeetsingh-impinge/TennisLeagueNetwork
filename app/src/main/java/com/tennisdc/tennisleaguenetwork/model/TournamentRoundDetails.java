package com.tennisdc.tennisleaguenetwork.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created on 28-May-17.
 */

public class TournamentRoundDetails {

    /*@SerializedName("player1")
    public String Player1;

    @SerializedName("player2")
    public String Player2;

    @SerializedName("score")
    public String Score;

    public String getPlayer1() {
        return Player1;
    }

    public void setPlayer1(String player1) {
        Player1 = player1;
    }

    public String getPlayer2() {
        return Player2;
    }

    public void setPlayer2(String player2) {
        Player2 = player2;
    }

    public String getScore() {
        return Score;
    }

    public void setScore(String score) {
        Score = score;
    }*/

    @SerializedName("player1")
    public TournamentPlayer Player1;

    @SerializedName("player2")
    public TournamentPlayer Player2;

    @SerializedName("score")
    public String Score;

    public TournamentPlayer getPlayer1() {
        return Player1;
    }

    public void setPlayer1(TournamentPlayer player1) {
        Player1 = player1;
    }

    public TournamentPlayer getPlayer2() {
        return Player2;
    }

    public void setPlayer2(TournamentPlayer player2) {
        Player2 = player2;
    }

    public String getScore() {
        return Score;
    }

    public void setScore(String score) {
        Score = score;
    }
}
