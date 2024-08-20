package com.tennisdc.tln.model;

import com.tennisdc.tln.common.ScoreFormat;

import org.parceler.Parcel;

import java.util.Date;
import com.tennisdc.tennisleaguenetwork.model.Court;

/**
 * Created  on 22-01-2015.
 */
@Parcel
public class MatchStats {

    public NameIdPair Competition;
    public Court Court;

    public long Date = new Date().getTime();
    public long Hours;
    public long Minutes;

    public String ScoreFormatName;

    public PlayerScore Winner, Looser;

    public String Comment;

    public boolean IsTie, IsLateCancel, IsNoShow, IsRetired, IsHitAround, IsShowWalkOver;

    /*"tie":"0",
            "late_cancel":"0",
            "set_tie":"0",
            "no_show":"0"*/

    public MatchStats() {}

    public boolean isScoreValid() {
        return Winner.totalScore() >= Looser.totalScore();
    }

    public ScoreFormat getScoreFormat() {
        return ScoreFormat.valueOf(ScoreFormatName);
    }

}
