package com.tennisdc.tennisleaguenetwork.model;

import org.parceler.Parcel;

/**
 * Created  on 22-01-2015.
 */
@Parcel
public class ScoreRange {
    public int min, max;

    public ScoreRange(){}

    public ScoreRange(int min, int max) {
        this.min = min;
        this.max = max;
    }
}
