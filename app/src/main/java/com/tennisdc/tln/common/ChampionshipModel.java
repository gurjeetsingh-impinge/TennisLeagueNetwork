package com.tennisdc.tln.common;

import java.util.List;

public class ChampionshipModel {

    /**
     * responseCode : 200
     * responseMessage : Success
     * competitions : [{"name":"Boston Championship","link":"https://www.tennisnortheast.com/info/2020_Boston_Championship"},{"name":"East Coast Championship","link":"https://www.tennisnortheast.com/info/2020_East_Coast_Championship"},{"name":"National Championship","link":"https://www.tennisnortheast.com/info/2020_EOY_MIAMI_tourney"}]
     */

    private String responseCode;
    private String responseMessage;
    private List<CompetitionsBean> competitions;

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public List<CompetitionsBean> getCompetitions() {
        return competitions;
    }

    public void setCompetitions(List<CompetitionsBean> competitions) {
        this.competitions = competitions;
    }

    public static class CompetitionsBean {
        /**
         * name : Boston Championship
         * link : https://www.tennisnortheast.com/info/2020_Boston_Championship
         */

        private String name;
        private String link;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }
}
