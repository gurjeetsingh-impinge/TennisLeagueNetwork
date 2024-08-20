package com.tennisdc.tln.common;

import com.tennisdc.tln.model.ScoreRange;

/**
 * Created  on 22-01-2015.
 */
public enum ScoreFormat {

    FORMAT_1("2 out of 3 sets", "SET", 3, new String[]{"Set 1", "Set 2", "Set 3"}, new ScoreRange[]{new ScoreRange(0, 12), new ScoreRange(0, 7), new ScoreRange(0, 7)}, new int[]{6, 6, 0}),
    FORMAT_2("2 sets w/3rd set tie-breaker", "TIE_BREAK", 3, new String[]{"Set 1", "Set 2", "Tie Break"}, new ScoreRange[]{new ScoreRange(0, 12), new ScoreRange(0, 7), new ScoreRange(0, 25)}, new int[]{6, 6, 6}),
    FORMAT_3("10 Game Pro set", "PROSET", 1, new String[]{"Set 1"}, new ScoreRange[]{new ScoreRange(0, 12)}, new int[]{10, 0, 0}),
    FORMAT_4("Fast Tennis", "FAST_TENNIS", 3, new String[]{"Set 1", "Set 2", "Set 3"}, new ScoreRange[]{new ScoreRange(0, 4),new ScoreRange(0, 4),new ScoreRange(0, 4)}, new int[]{4, 4, 0}) ;

    String name;
    private String internalName;

    int setCount;
    String[] setLabels;
    ScoreRange[] scoreRanges;
    int[] winnerDefaultScore;

    ScoreFormat(String aState, String internalName, int setCount, String[] setLabels, ScoreRange[] setRanges, int[] winnerDefaultScore) {
        this.name = aState;
        this.internalName = internalName;
        this.setCount = setCount;
        this.setLabels = setLabels;
        this.scoreRanges = setRanges;
        this.winnerDefaultScore = winnerDefaultScore;
    }

    public int getSetCount() {
        return setCount;
    }

    public ScoreRange[] getScoreRanges() {
        return scoreRanges;
    }

    public String[] getSetLabels() {
        return setLabels;
    }

    public int[] getWinnerDefaultScore() {
        return winnerDefaultScore;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getInternalName() {
        return internalName;
    }
}

