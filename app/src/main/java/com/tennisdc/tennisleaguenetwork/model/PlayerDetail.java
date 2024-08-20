package com.tennisdc.tennisleaguenetwork.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created  on 02-02-2015.
 */
@Parcel
public class PlayerDetail {

    @SerializedName("player_id")
    public long id;

    @SerializedName("player_name")
    public String Name; //: "Khelifa, Alexandre",

    @SerializedName("status")
    public String status;

    @SerializedName("player_first_name")
    public String firstName;

    @SerializedName("win_los")
    public String WinLoss; //: "4 - 0",

    @SerializedName("games")
    public String Games; //: "0.712 (42-17)",

    @SerializedName("player_phone")
    public String Phone; //: "404-447-5737",

    @SerializedName("player_email")
    public String Email; // : "alexandre.khelifa@gmail.com",

    @SerializedName("profile_image")
    public String Image; // : "https://tennisleaguenetwork-profile-images.s3.amazonaws.com/pictures/12719/IMG-20140718-WA0005.jpg"

    @SerializedName("partner_program_enrolled")
    public boolean IsPartnerProgramEnrolled;

    @SerializedName("ladder_program_enrolled")
    public boolean IsLadderProgramEnrolled;

    @SerializedName("home_court")
    public Court HomeCourt;

    @SerializedName("city_name")
    public String CityName;

    @SerializedName("skill_rating")//: "Women's Rec. 2.5",
    public String SkillRating;

    @SerializedName("on_court_connections") //: "5",
    public String OnCourtConnections;

    @SerializedName("playing_region")//: "No. San Francisco",
    public String Region;

    @SerializedName("ladder_record")
    public String LadderRecord;

    @SerializedName("poty_rank")
    public int PotyRank;

    @SerializedName("show_challenge_text")
    public String ChallengeText;

    @SerializedName("show_challenge_button")
    public boolean IsChallengeButtonEnable;

    @SerializedName("call_setting")
    public boolean CanCall;//: true,

    @SerializedName("text_setting")
    public boolean CanText;//: true

    @SerializedName("seed_number")
    public int SeedNumber; //: 1,

    @SerializedName("season_record")
    public String SeasonRecord; //: "11 - 1",

    @SerializedName("overall_record")
    public String OverallRecord; //: "202 - 52",

    /*@SerializedName("veteran_discount")
    public int VeteranDiscount;*/

    @SerializedName("discount_array")
    public ArrayList<Discount> discount;

    @SerializedName("retired")
    public boolean IsRetired;

    @SerializedName("mobile_slider_popup_setting")
    public boolean mobilePopupVisible;

    public void setDummyData(String name, String email, String phone){
        this.Name = name;
        this.Email = email;
        this.Phone = phone;
    }
    public boolean isSelected;
}