package com.tennisdc.tln.model;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created on 11-Jul-17.
 */

public class UserDetails  {

    @SerializedName("id")
    public long id;

    @SerializedName("full_name")
    public String FullName;

    @SerializedName("first_name")
    public String FirstName;

    @SerializedName("last_name")
    public String LastName;

    @SerializedName("email_address")
    public String Email;

    @SerializedName("primary_telephone_number")
    public String Mobile;

    @SerializedName("mailing_address_1")
    public String Street;

    @SerializedName("city")
    public String City;

    @SerializedName("state")
    public String State;

    @SerializedName("status")
    public String Status;

    @SerializedName("court_id")
    public long Court_ID;

    @SerializedName("court_name")
    public String CourtName;

    @SerializedName("preferred_skill_id")
    public long PreferredSkillID;

    @SerializedName("daytime_play")
    public boolean DayTimePlay;

    @SerializedName("age_range")
    public String AgeRange;

    @SerializedName("member_since")
    public String MemberSince;

    @SerializedName("participating_city")
    public String ParticipatingCity;

    @SerializedName("playing_region")
    public String PlayingRegion;

    @SerializedName("tlns_player_rating")
    public String PlayerRating;

    @SerializedName("overall_league_record")
    public long OverallLeagueRecord;

    @SerializedName("player_of_the_year_points")
    public long PlayerOfTheYearPoints;

    @SerializedName("favorite_player")
    public String FavoritePlayer;

    @SerializedName("playoff_notifier")
    public boolean PlayoffNotifier;

    @SerializedName("ladder_score_notifier")
    public boolean LadderScoreNotifier;

    @SerializedName("division_score_notifier")
    public boolean DivisionScoreNotifier;

    @SerializedName("dob")
    public String dob;

    public long getId() {
        return id;
    }

    public String getFullName() {
        return FullName;
    }

    public String getFirstName() {
        return FirstName;
    }

    public String getLastName() {
        return LastName;
    }

    public String getEmail() {
        return Email;
    }

    public String getMobile() {
        return Mobile;
    }

    public String getStreet() {
        return Street;
    }

    public String getCity() {
        return City;
    }

    public String getState() {
        return State;
    }

    public String getStatus() {
        return Status;
    }

    public long getCourt_ID() {
        return Court_ID;
    }

    public String getCourtName() {
        return CourtName;
    }

    public long getPreferredSkillID() {
        return PreferredSkillID;
    }

    public boolean isDayTimePlay() {
        return DayTimePlay;
    }

    public String getAgeRange() {
        return AgeRange;
    }

    public String getDOB() {
        if(dob != null)
        {
            try {
                SimpleDateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd");
                Date da = (Date)inputFormatter.parse(dob);

                SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM - YYYY");

                String date_range = monthFormat.format(da);
                return date_range;
            }
            catch(Exception e) {
                return "Month Year";
            }
        }
        return "Month Year";
    }

    public String getMemberSince() {
        return MemberSince;
    }

    public String getParticipatingCity() {
        return ParticipatingCity;
    }

    public String getPlayingRegion() {
        return PlayingRegion;
    }

    public String getPlayerRating() {
        return PlayerRating;
    }

    public long getOverallLeagueRecord() {
        return OverallLeagueRecord;
    }

    public long getPlayerOfTheYearPoints() {
        return PlayerOfTheYearPoints;
    }

    public String getFavoritePlayer() {
        return FavoritePlayer;
    }

    public boolean isPlayoffNotifier() {
        return PlayoffNotifier;
    }

    public boolean isLadderScoreNotifier() {
        return LadderScoreNotifier;
    }

    public boolean isDivisionScoreNotifier() {
        return DivisionScoreNotifier;
    }
}
