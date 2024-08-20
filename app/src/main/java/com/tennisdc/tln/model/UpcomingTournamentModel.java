package com.tennisdc.tln.model;

import java.util.List;

public class UpcomingTournamentModel {

    /**
     * responseCode : 200
     * start_date : 2019-09-19T00:00:00Z
     * competition_name : Sept. Tourney
     * division_name : Enrollment Page
     * skill_name : All Skill Levels
     * player_standing1 : [{"player_name":"R. Campbell","actual_league_rating":"Men's Skilled 3.0","league_matches":"8"},{"player_name":"L. Nguyen","actual_league_rating":"Men's Competitive1 3.5","league_matches":"10"},{"player_name":"D. Owens","actual_league_rating":"Men's Advanced1 4.0","league_matches":"10"},{"player_name":"I. Akeel","actual_league_rating":"Men's Advanced1 4.0","league_matches":"83"},{"player_name":"Z. Smith","actual_league_rating":"Women's Beginner 2.0","league_matches":"4"},{"player_name":"J. Washington","actual_league_rating":"Women's Rec. 2.5","league_matches":"0"},{"player_name":"R. Ahuja","actual_league_rating":"Men's Skilled 3.0","league_matches":"0"},{"player_name":"D. Martinez","actual_league_rating":"Men's Elite2 4.25","league_matches":"6"}]
     * player_standing2 : []
     * enrolled : false
     * enrolledMessage : Below players are new to the league and have probational ratings, by joining they've agreed to play in the a higher bracket then their probational rating.
     */

    private String responseCode;
    private String start_date;
    private String competition_name;
    private String division_name;
    private String skill_name;
    private boolean enrolled;
    private String enrolledMessage;
    private List<PlayerStanding1Bean> player_standing1;
    private List<PlayerStanding1Bean> player_standing2;

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getCompetition_name() {
        return competition_name;
    }

    public void setCompetition_name(String competition_name) {
        this.competition_name = competition_name;
    }

    public String getDivision_name() {
        return division_name;
    }

    public void setDivision_name(String division_name) {
        this.division_name = division_name;
    }

    public String getSkill_name() {
        return skill_name;
    }

    public void setSkill_name(String skill_name) {
        this.skill_name = skill_name;
    }

    public boolean isEnrolled() {
        return enrolled;
    }

    public void setEnrolled(boolean enrolled) {
        this.enrolled = enrolled;
    }

    public String getEnrolledMessage() {
        return enrolledMessage;
    }

    public void setEnrolledMessage(String enrolledMessage) {
        this.enrolledMessage = enrolledMessage;
    }

    public List<PlayerStanding1Bean> getPlayer_standing1() {
        return player_standing1;
    }

    public void setPlayer_standing1(List<PlayerStanding1Bean> player_standing1) {
        this.player_standing1 = player_standing1;
    }

    public List<PlayerStanding1Bean> getPlayer_standing2() {
        return player_standing2;
    }

    public void setPlayer_standing2(List<PlayerStanding1Bean> player_standing2) {
        this.player_standing2 = player_standing2;
    }

    public static class PlayerStanding1Bean {
        /**
         * player_name : R. Campbell
         * actual_league_rating : Men's Skilled 3.0
         * league_matches : 8
         */

        private String player_name;
        private String actual_league_rating;
        private String league_matches;
        private boolean profile_active;
        private int player_id;

        public String getPlayer_name() {
            return player_name;
        }

        public void setPlayer_name(String player_name) {
            this.player_name = player_name;
        }

        public String getActual_league_rating() {
            return actual_league_rating;
        }

        public void setActual_league_rating(String actual_league_rating) {
            this.actual_league_rating = actual_league_rating;
        }

        public String getLeague_matches() {
            return league_matches;
        }

        public void setLeague_matches(String league_matches) {
            this.league_matches = league_matches;
        }

        public boolean isProfile_active() {
            return profile_active;
        }

        public void setProfile_active(boolean profile_active) {
            this.profile_active = profile_active;
        }

        public int getPlayer_id() {
            return player_id;
        }

        public void setPlayer_id(int player_id) {
            this.player_id = player_id;
        }
    }
}
