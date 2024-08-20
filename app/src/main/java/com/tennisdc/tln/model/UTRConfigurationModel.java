package com.tennisdc.tln.model;

public class UTRConfigurationModel {

    /**
     * responseCode : 200
     * question1_title : dfdf
     * question2_title : dfdf
     * question1 : 0
     * question2 : 0
     * display_question2 : true
     * display_question2_no : false
     */

    private int responseCode;
    private String question1_title;
    private String question2_title;
    private int question1;
    private int question2;
    private boolean display_question2;
    private boolean display_question2_no;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getQuestion1_title() {
        return question1_title;
    }

    public void setQuestion1_title(String question1_title) {
        this.question1_title = question1_title;
    }

    public String getQuestion2_title() {
        return question2_title;
    }

    public void setQuestion2_title(String question2_title) {
        this.question2_title = question2_title;
    }

    public int getQuestion1() {
        return question1;
    }

    public void setQuestion1(int question1) {
        this.question1 = question1;
    }

    public int getQuestion2() {
        return question2;
    }

    public void setQuestion2(int question2) {
        this.question2 = question2;
    }

    public boolean isDisplay_question2() {
        return display_question2;
    }

    public void setDisplay_question2(boolean display_question2) {
        this.display_question2 = display_question2;
    }

    public boolean isDisplay_question2_no() {
        return display_question2_no;
    }

    public void setDisplay_question2_no(boolean display_question2_no) {
        this.display_question2_no = display_question2_no;
    }
}
