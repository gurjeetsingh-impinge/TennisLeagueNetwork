//package com.tennisdc.tln.model;
//
//import com.google.gson.annotations.SerializedName;
//
//import io.realm.RealmList;
//import io.realm.RealmObject;
//import io.realm.annotations.PrimaryKey;
//
///**
// * Created  on 2015-03-12.
// */
//public class Location extends RealmObject {
//
//    @PrimaryKey
//    @SerializedName("id")
//    private long id;
//
//    @SerializedName("name")
//    private String name;
//
//    @SerializedName("court_regions")
//    private RealmList<CourtRegion> courtRegions;
//
//    public long getId() {
//        return id;
//    }
//
//    public void setId(long id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public RealmList<CourtRegion> getCourtRegions() {
//        return courtRegions;
//    }
//
//    public void setCourtRegions(RealmList<CourtRegion> courtRegions) {
//        this.courtRegions = courtRegions;
//    }
//}
