package com.tennisdc.tln.model;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CourtDetailModel extends RealmObject {


    /**
     * id : 163
     * name :  Mid - Smith Park
     * location_id : 2
     * court_region_id : 91
     * total_courts : 2
     * indoor_courts : 0
     * clay_courts : 0
     * lighted_courts : 0
     * fee : 0
     * phone_number :
     * address : N Rockwell St & W Huron St
     * city : Chicago
     * state : IL
     * zip_code : 60612
     * website :
     * notes : Courts are in bad shape.
     * overall_rating : 2.6
     * surface_rating : 2.8
     * rating_crowds : 3.4
     * rating_lights : null
     * rating_management : null
     * has_hitting_wall : 0
     * bathroom_available : 0
     * water_access : 0
     * has_fee : 0
     * has_store : 0
     * is_club : 0
     * restricted : 0
     * matches_played_here : 48
     * number_of_ratings : (1 rating(s) for this court)
     * comments : [{"content":"Great courts but there are only two...","player_name":"Pete Oyler","comment_date":"August 14th, 2018"}]
     */
    @PrimaryKey
    private int id;
    private String name;
    private int location_id;
    private int court_region_id;
    private int total_courts;
    private int indoor_courts;
    private int clay_courts;
    private int lighted_courts;
    private int fee;
    private String phone_number;
    private String address;
    private String city;
    private String state;
    private String zip_code;
    private String website;
    private String notes;
    private double overall_rating;
    private double surface_rating;
    private double rating_crowds;
    private double rating_lights;
    private double rating_management;
    private double parent_lat;
    private double parent_long;
    private int has_hitting_wall;
    private int bathroom_available;
    private int water_access;
    private int has_fee;
    private int has_store;
    private int is_club;
    private int restricted;
    private int matches_played_here;
    private String number_of_ratings;
    private RealmList<CoomentsBean> comments;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLocation_id() {
        return location_id;
    }

    public void setLocation_id(int location_id) {
        this.location_id = location_id;
    }

    public int getCourt_region_id() {
        return court_region_id;
    }

    public void setCourt_region_id(int court_region_id) {
        this.court_region_id = court_region_id;
    }

    public int getTotal_courts() {
        return total_courts;
    }

    public void setTotal_courts(int total_courts) {
        this.total_courts = total_courts;
    }

    public int getIndoor_courts() {
        return indoor_courts;
    }

    public void setIndoor_courts(int indoor_courts) {
        this.indoor_courts = indoor_courts;
    }

    public int getClay_courts() {
        return clay_courts;
    }

    public void setClay_courts(int clay_courts) {
        this.clay_courts = clay_courts;
    }

    public int getLighted_courts() {
        return lighted_courts;
    }

    public void setLighted_courts(int lighted_courts) {
        this.lighted_courts = lighted_courts;
    }

    public int getFee() {
        return fee;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public double getOverall_rating() {
        return overall_rating;
    }

    public void setOverall_rating(double overall_rating) {
        this.overall_rating = overall_rating;
    }

    public double getSurface_rating() {
        return surface_rating;
    }

    public void setSurface_rating(double surface_rating) {
        this.surface_rating = surface_rating;
    }

    public double getRating_crowds() {
        return rating_crowds;
    }

    public void setRating_crowds(double rating_crowds) {
        this.rating_crowds = rating_crowds;
    }

    public double getRating_lights() {
        return rating_lights;
    }

    public void setRating_lights(double rating_lights) {
        this.rating_lights = rating_lights;
    }

    public double getRating_management() {
        return rating_management;
    }

    public void setRating_management(double rating_management) {
        this.rating_management = rating_management;
    }

    public double getParent_lat() {
        return parent_lat;
    }

    public void setParent_lat(double parent_lat) {
        this.parent_lat = parent_lat;
    }

    public double getParent_long() {
        return parent_long;
    }

    public void setParent_long(double parent_long) {
        this.parent_long = parent_long;
    }

    public int getHas_hitting_wall() {
        return has_hitting_wall;
    }

    public void setHas_hitting_wall(int has_hitting_wall) {
        this.has_hitting_wall = has_hitting_wall;
    }

    public int getBathroom_available() {
        return bathroom_available;
    }

    public void setBathroom_available(int bathroom_available) {
        this.bathroom_available = bathroom_available;
    }

    public int getWater_access() {
        return water_access;
    }

    public void setWater_access(int water_access) {
        this.water_access = water_access;
    }

    public int getHas_fee() {
        return has_fee;
    }

    public void setHas_fee(int has_fee) {
        this.has_fee = has_fee;
    }

    public int getHas_store() {
        return has_store;
    }

    public void setHas_store(int has_store) {
        this.has_store = has_store;
    }

    public int getIs_club() {
        return is_club;
    }

    public void setIs_club(int is_club) {
        this.is_club = is_club;
    }

    public int getRestricted() {
        return restricted;
    }

    public void setRestricted(int restricted) {
        this.restricted = restricted;
    }

    public int getMatches_played_here() {
        return matches_played_here;
    }

    public void setMatches_played_here(int matches_played_here) {
        this.matches_played_here = matches_played_here;
    }

    public String getNumber_of_ratings() {
        return number_of_ratings;
    }

    public void setNumber_of_ratings(String number_of_ratings) {
        this.number_of_ratings = number_of_ratings;
    }


    public RealmList<CoomentsBean> getComments() {
        return comments;
    }

    public void setComments(RealmList<CoomentsBean> comments) {
        this.comments = comments;
    }
}

