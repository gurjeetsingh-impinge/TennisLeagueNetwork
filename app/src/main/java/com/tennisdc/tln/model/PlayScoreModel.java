package com.tennisdc.tln.model;

public class PlayScoreModel {

    /**
     * id : 213542
     * date : 05/02/19
     * winner : S. Chagnon
     * opponent : M. Caron
     * score : 6-0; 6-0
     */

    private int id;
    private String date;
    private String winner;
    private String opponent;
    private String score;
    private String result;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
