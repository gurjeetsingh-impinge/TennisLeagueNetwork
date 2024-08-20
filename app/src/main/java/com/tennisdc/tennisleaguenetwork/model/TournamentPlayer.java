package com.tennisdc.tennisleaguenetwork.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created on 21-11-2017.
 */

public class TournamentPlayer {

    @SerializedName("name")
    public String Name;

    @SerializedName("phone")
    public String Phone;

    @SerializedName("email")
    public String Email;

    @SerializedName("winner")
    public boolean Winner;

    @SerializedName("contact")
    public boolean Contact;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public boolean isWinner() {
        return Winner;
    }

    public void setWinner(boolean winner) {
        Winner = winner;
    }

    public boolean isContact() {
        return Contact;
    }

    public void setContact(boolean contact) {
        Contact = contact;
    }
}
