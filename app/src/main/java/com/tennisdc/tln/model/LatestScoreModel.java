package com.tennisdc.tln.model;

import java.util.ArrayList;
import java.util.List;

public class LatestScoreModel {

    /**
     * responseCode : 200
     * scores : [{"title":"Spring #1","scores":[{"date":"04/14/19","program":"Spring #1","division_level":"Men's Advanced1 4.0","winner_name":"Jigar Jain","opponent_name":"John Steies","score":"6-2; 6-2"},{"date":"04/13/19","program":"Spring #1","division_level":"Men's Advanced1 4.0","winner_name":"Eric Klein","opponent_name":"Francisco Alonso","score":"10-8"},{"date":"04/13/19","program":"Spring #1","division_level":"Men's Advanced1 4.0","winner_name":"John Kumpf","opponent_name":"John Steies","score":"6-2; 7-6"}]},{"title":"Other","scores":[{"date":"04/14/19","program":"Spring #1","division_level":"Men's Advanced1 4.0","winner_name":"Ibrahim Akeel","opponent_name":"Andrew Gordetsky","score":"6-4; 6-7; <br/>TB 10-6"},{"date":"04/14/19","program":"Non-League","division_level":"","winner_name":"Sara Prell","opponent_name":"Lynn Delacey","score":"7-5; 6-4"},{"date":"04/14/19","program":"Spring #1","division_level":"Men's Skilled 3.0","winner_name":"Sumit Satarkar","opponent_name":"Michael Erwin","score":"7-5; 3-4"},{"date":"04/14/19","program":"Spring #1","division_level":"Men's Competitive2 3.25","winner_name":"Eli Schuman","opponent_name":"Anthony Diana","score":"6-4; 5-7; 7-6"},{"date":"04/13/19","program":"Spring #1","division_level":"Women's Advanced2 3.75","winner_name":"Anna Bongers","opponent_name":"Olivia Hegner","score":"3-6; 6-1; <br/>TB 10-6"},{"date":"04/13/19","program":"Spring #1","division_level":"Men's Advanced1 4.0","winner_name":"Minal Caron","opponent_name":"Ibrahim Akeel","score":"6-2; 6-3"},{"date":"04/07/19","program":"Division A Playoffs","division_level":"","winner_name":"David Levine","opponent_name":"Chris Paciello","score":"6-4; 6-1"},{"date":"04/06/19","program":"WTT","division_level":"Men's Advanced1 4.0","winner_name":" Serves You Right","opponent_name":" Acme Racquet","score":"31-19"},{"date":"04/06/19","program":"WTT","division_level":"Men's Advanced2 3.75","winner_name":" Net Results","opponent_name":" Aces High","score":"29-15"},{"date":"04/06/19","program":"WTT","division_level":"Men's Competitive1 3.5","winner_name":" Line Painters","opponent_name":" More Cowbell","score":"26-23"},{"date":"04/06/19","program":"WTT","division_level":"Men's Competitive2 3.25","winner_name":"  Swipers","opponent_name":" New Strings","score":"28-19"},{"date":"04/06/19","program":"WTT","division_level":"Men's Advanced2 3.75","winner_name":" No Excuses","opponent_name":" Tightly Strung","score":"31-18"},{"date":"04/06/19","program":"WTT","division_level":"Men's Advanced2 3.75","winner_name":" Tennis NorthEast.com","opponent_name":" Bye Bye Love","score":"35-21"},{"date":"04/06/19","program":"WTT","division_level":"Men's Advanced1 4.0","winner_name":" Everlasting Lobstoppers","opponent_name":" The Blades","score":"28-24"},{"date":"04/06/19","program":"WTT","division_level":"Men's Competitive1 3.5","winner_name":" Racketeers","opponent_name":" Rebound","score":"32-26"},{"date":"04/06/19","program":"WTT","division_level":"Men's Competitive2 3.25","winner_name":" Racquet Rockets","opponent_name":" Terminetters","score":"33-16"},{"date":"04/06/19","program":"WTT","division_level":"Men's Competitive2 3.25","winner_name":" Net Prophets","opponent_name":" Baseliners","score":"26-24"},{"date":"04/03/19","program":"Division B Playoffs","division_level":"","winner_name":"Peter Zhu","opponent_name":"Kunal Vaidya","score":"6-2; 0-6; <br/>TB 10-6"},{"date":"04/03/19","program":"Division A Playoffs","division_level":"","winner_name":"David Levine","opponent_name":"Shalin Shah","score":"10-5"},{"date":"04/02/19","program":"Division A Playoffs","division_level":"","winner_name":"Chris Paciello","opponent_name":"Rich Greif","score":"7-5; 6-2"},{"date":"03/30/19","program":"WTT","division_level":"Men's Advanced1 4.0","winner_name":" Love Actually","opponent_name":" The Blades","score":"36-13"},{"date":"03/30/19","program":"WTT","division_level":"Men's Advanced2 3.75","winner_name":" Tennis NorthEast.com","opponent_name":" No Excuses","score":"30-16"},{"date":"03/30/19","program":"WTT","division_level":"Men's Advanced1 4.0","winner_name":" Serves You Right","opponent_name":" Everlasting Lobstoppers","score":"29-20"},{"date":"03/30/19","program":"WTT","division_level":"Men's Advanced1 4.0","winner_name":" Acme Racquet","opponent_name":" You Got Served","score":"31-17"},{"date":"03/30/19","program":"WTT","division_level":"Men's Competitive1 3.5","winner_name":" Rebound","opponent_name":" Set 2 Win","score":"26-20"},{"date":"03/30/19","program":"WTT","division_level":"Men's Competitive1 3.5","winner_name":" Strokes of Luck","opponent_name":" Line Painters","score":"32-14"},{"date":"03/30/19","program":"WTT","division_level":"Men's Competitive1 3.5","winner_name":" More Cowbell","opponent_name":" Racketeers","score":"32-24"},{"date":"03/30/19","program":"WTT","division_level":"Men's Advanced2 3.75","winner_name":" Net Results","opponent_name":" Bye Bye Love","score":"29-18"},{"date":"03/30/19","program":"WTT","division_level":"Men's Competitive2 3.25","winner_name":" Net Prophets","opponent_name":" Racquet Rockets","score":"32-14"},{"date":"03/30/19","program":"WTT","division_level":"Men's Competitive2 3.25","winner_name":"  Swipers","opponent_name":" Baseliners","score":"33-16"},{"date":"03/29/19","program":"Division A Playoffs","division_level":"","winner_name":"David Levine","opponent_name":"Al Ganeshkumar","score":"10-4"},{"date":"03/26/19","program":"Division B Playoffs","division_level":"","winner_name":"Kunal Vaidya","opponent_name":"Won Bok Lee","score":"6-3; 0-6; <br/>TB 10-8"},{"date":"03/25/19","program":"WTT","division_level":"Men's Competitive2 3.25","winner_name":" New Strings","opponent_name":" Baseliners","score":"29-24"},{"date":"03/25/19","program":"WTT","division_level":"Men's Competitive2 3.25","winner_name":" Terminetters","opponent_name":" Net Prophets","score":"27-23"},{"date":"03/25/19","program":"WTT","division_level":"Men's Competitive2 3.25","winner_name":"  Swipers","opponent_name":" Racquet Rockets","score":"31-25"},{"date":"03/25/19","program":"WTT","division_level":"Men's Competitive1 3.5","winner_name":" More Cowbell","opponent_name":" Set 2 Win","score":"30-19"},{"date":"03/25/19","program":"WTT","division_level":"Men's Advanced2 3.75","winner_name":" No Excuses","opponent_name":" Net Results","score":"30-16"},{"date":"03/25/19","program":"WTT","division_level":"Men's Advanced2 3.75","winner_name":" Bye Bye Love","opponent_name":" Aces High","score":"24-18"},{"date":"03/25/19","program":"WTT","division_level":"Men's Advanced1 4.0","winner_name":" The Blades","opponent_name":" You Got Served","score":"25-24"},{"date":"03/25/19","program":"WTT","division_level":"Men's Advanced2 3.75","winner_name":" Tightly Strung","opponent_name":" Tennis NorthEast.com","score":"31-22"},{"date":"03/25/19","program":"WTT","division_level":"Men's Competitive1 3.5","winner_name":" Rebound","opponent_name":" Strokes of Luck","score":"27-27"},{"date":"03/25/19","program":"WTT","division_level":"Men's Advanced1 4.0","winner_name":" Serves You Right","opponent_name":" Love Actually","score":"29-18"}]}]
     * title : 26,956 matches played and counting...
     */

//    private String responseCode;
    private String title;
    private ArrayList<ScoresBean> scores;

//    public String getResponseCode() {
//        return responseCode;
//    }
//
//    public void setResponseCode(String responseCode) {
//        this.responseCode = responseCode;
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<ScoresBean> getScores() {
        return scores;
    }

    public void setScores(ArrayList<ScoresBean> scores) {
        this.scores = scores;
    }
    public static class ScoresBean {
        /**
         * date : 04/14/19
         * program : Spring #1
         * division_level : Men's Advanced1 4.0
         * winner_name : Jigar Jain
         * opponent_name : John Steies
         * score : 6-2; 6-2
         */

        private String date;
        private String program;
        private String division_level;
        private String winner_name;
        private String opponent_name;
        private String score;
        private String match_location;
        private String competition_level;
        private boolean new_score;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getProgram() {
            return program;
        }

        public void setProgram(String program) {
            this.program = program;
        }

        public String getDivision_level() {
            return division_level;
        }

        public void setDivision_level(String division_level) {
            this.division_level = division_level;
        }

        public String getWinner_name() {
            return winner_name;
        }

        public void setWinner_name(String winner_name) {
            this.winner_name = winner_name;
        }

        public String getOpponent_name() {
            return opponent_name;
        }

        public void setOpponent_name(String opponent_name) {
            this.opponent_name = opponent_name;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getMatch_location() {
            return match_location;
        }

        public void setMatch_location(String match_location) {
            this.match_location = match_location;
        }

        public String getCompetition_level() {
            return competition_level;
        }

        public void setCompetition_level(String competition_level) {
            this.competition_level = competition_level;
        }

        public boolean getNewScore() {
            return new_score;
        }

        public void setNewScore(boolean new_score) {
            this.new_score = new_score;
        }
    }
//    public static class ScoresBeanX {
//        /**
//         * title : Spring #1
//         * scores : [{"date":"04/14/19","program":"Spring #1","division_level":"Men's Advanced1 4.0","winner_name":"Jigar Jain","opponent_name":"John Steies","score":"6-2; 6-2"},{"date":"04/13/19","program":"Spring #1","division_level":"Men's Advanced1 4.0","winner_name":"Eric Klein","opponent_name":"Francisco Alonso","score":"10-8"},{"date":"04/13/19","program":"Spring #1","division_level":"Men's Advanced1 4.0","winner_name":"John Kumpf","opponent_name":"John Steies","score":"6-2; 7-6"}]
//         */
//
//        private String title;
//        private List<ScoresBean> scores;
//
//        public String getTitle() {
//            return title;
//        }
//
//        public void setTitle(String title) {
//            this.title = title;
//        }
//
//        public List<ScoresBean> getScores() {
//            return scores;
//        }
//
//        public void setScores(List<ScoresBean> scores) {
//            this.scores = scores;
//        }
//
//        public static class ScoresBean {
//            /**
//             * date : 04/14/19
//             * program : Spring #1
//             * division_level : Men's Advanced1 4.0
//             * winner_name : Jigar Jain
//             * opponent_name : John Steies
//             * score : 6-2; 6-2
//             */
//
//            private String date;
//            private String program;
//            private String division_level;
//            private String winner_name;
//            private String opponent_name;
//            private String score;
//
//            public String getDate() {
//                return date;
//            }
//
//            public void setDate(String date) {
//                this.date = date;
//            }
//
//            public String getProgram() {
//                return program;
//            }
//
//            public void setProgram(String program) {
//                this.program = program;
//            }
//
//            public String getDivision_level() {
//                return division_level;
//            }
//
//            public void setDivision_level(String division_level) {
//                this.division_level = division_level;
//            }
//
//            public String getWinner_name() {
//                return winner_name;
//            }
//
//            public void setWinner_name(String winner_name) {
//                this.winner_name = winner_name;
//            }
//
//            public String getOpponent_name() {
//                return opponent_name;
//            }
//
//            public void setOpponent_name(String opponent_name) {
//                this.opponent_name = opponent_name;
//            }
//
//            public String getScore() {
//                return score;
//            }
//
//            public void setScore(String score) {
//                this.score = score;
//            }
//        }
//    }
}
