package com.tennisdc.tln.model;

import android.widget.NumberPicker;

import com.tennisdc.tln.common.ScoreFormat;

import org.parceler.Parcel;

/**
 * Created  on 22-01-2015.
 */
@Parcel
public class PlayerScore {

    public NameIdPair Player;

    int set1;
    int set2;
    int set3;
    int set3_tie;

    public int getSet1() {
        return set1;
    }

    public int getSet2() {
        return set2;
    }

    public int getSet3() {
        return set3;
    }

    public int getSet3Tie() {
        return set3_tie;
    }

    public void setSet1(int mValue) {
        set1 = mValue;
    }

    public void setSet2(int mValue) {
        set2 = mValue;
    }

    public void setSet3(int mValue) {
        set3 = mValue;
    }

    public void setSet3Tie(int mValue) {
        set3_tie = mValue;
    }

    public int totalScore() {
        return set1 + set2 + set3 + set3_tie;
    }

    public void submitScore(NumberPicker[] numberPickers, ScoreFormat scoreFormat) {
        set1 = set2 = set3 = set3_tie = 0;
        switch (scoreFormat) {
            case FORMAT_1:
                set1 = numberPickers[0].getValue();
                set2 = numberPickers[1].getValue();
                set3 = numberPickers[2].getValue();
                break;

            case FORMAT_2:
                set1 = numberPickers[0].getValue();
                set2 = numberPickers[1].getValue();
                set3_tie = numberPickers[2].getValue();
                break;

            case FORMAT_3:
                set1 = numberPickers[0].getValue();
                break;

            case FORMAT_4:
                set1 = numberPickers[0].getValue();
                set2 = numberPickers[1].getValue();
                set3 = numberPickers[2].getValue();
                break;
        }
    }

    public String getScoreString(ScoreFormat scoreFormat) {
        switch (scoreFormat) {
            case FORMAT_1:
                return set1 + " - " + set2 + " - " + set3;

            case FORMAT_2:
                return set1 + " - " + set2 + " - " + set3_tie;

            case FORMAT_3:
                return set1 + "";

            case FORMAT_4:
                return set1 + " - " + set2 + " - " + set3;
        }
        return null;
    }

    public static boolean isValidScore(PlayerScore winnerScore, PlayerScore opponentScore) {
        int playerWin = 0, opponentWin = 0;

        if(winnerScore.getSet1() > opponentScore.getSet1()) playerWin++;
        else if(winnerScore.getSet1() < opponentScore.getSet1()) opponentWin++;

        if(winnerScore.getSet2() > opponentScore.getSet2()) playerWin++;
        else if(winnerScore.getSet2() < opponentScore.getSet2()) opponentWin++;

        if(winnerScore.getSet3() > opponentScore.getSet3())  playerWin++;
        else if(winnerScore.getSet3() < opponentScore.getSet3()) opponentWin++;

        if(winnerScore.getSet3Tie() > opponentScore.getSet3Tie()) playerWin++;
        else if(winnerScore.getSet3Tie() < opponentScore.getSet3Tie()) opponentWin++;

        return playerWin > opponentWin;
    }

    public static boolean isWinnersValidScore(PlayerScore winnerScore, PlayerScore opponentScore){
        if(winnerScore.getSet1() > opponentScore.getSet1()
                && winnerScore.getSet2() > opponentScore.getSet2()
                && (winnerScore.getSet3()>0 || opponentScore.getSet3()>0)) {

            return false;
        }

        else
            return true;
    }

}
