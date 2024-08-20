//package com.tennisdc.tln.model;
//
//import com.google.gson.annotations.SerializedName;
//
//import org.parceler.Parcel;
//
//import java.util.List;
//
//import io.realm.RealmList;
//import io.realm.RealmObject;
//import io.realm.annotations.PrimaryKey;
//
///**
// * Created  on 2015-03-12.
// */
//@Parcel(value = Parcel.Serialization.BEAN, analyze = {Court.class})
//public class Court extends RealmObject {
//
//    @PrimaryKey
//    @SerializedName("id")
//    private long id;
//
//    @SerializedName("location_id")
//    private long locationId; //:"74",
//
//    @SerializedName("court_region_id")
//    private long regionId; //:"146",
//
//    @SerializedName("name")
//    private String name;
//
//    @SerializedName("phone_number")
//    private String phone;    //:"8786354563",
//
//    @SerializedName("website")
//    private String website;  //:"rc.com",
//
//    @SerializedName("address")
//    private String address; //: "340 W Alexander Blvd., Elmhurst IL  60126",
//
//    @SerializedName("city")
//    private String city; //:"city",
//
//    @SerializedName("state")
//    private String state;    //:"state",
//
//    @SerializedName("zip_code")
//    private String zipCode;  //:"10011",
//
//    @SerializedName("latitude") //: "41.8978442",
//    private double latitude;
//
//    @SerializedName("longitude")    //: "-87.9509467"
//    private double longitude;
//
//    @SerializedName("notes")
//    private String notes;    //:"notes"
//
//    @SerializedName("is_club")
//    private int isClub; //:"1",
//
//    @SerializedName("has_store")
//    private int hasStore; //:"1",
//
//    @SerializedName("has_stringer")
//    private int hasStringer; //:"1",
//
//    @SerializedName("total_courts")
//    private int totalCourts; //:"1",
//
//    @SerializedName("indoor_courts")
//    private int indoorCourts;    //:"1",
//
//    @SerializedName("clay_courts")
//    private int clayCourts;     //:"1",
//
//    @SerializedName("lighted_courts")
//    private int lightedCourts;   //:"2",
//
//    @SerializedName("has_hitting_wall")
//    private int hasHittingWall;  //:"1",
//
//    @SerializedName("restricted")
//    private int restricted;  //:"1",
//
//    @SerializedName("bathroom_available")
//    private int hasBathroom; //:"1",
//
//    @SerializedName("water_access")
//    private int hasWater;    //:"1",
//
//    @SerializedName("has_fee")
//    private int hasFee;  //:"1",
//
//    @SerializedName("fee")
//    private float fee;   //:"2",
//
//    @SerializedName("overall_rating")
//    private float overallRating; //: 0,
//
//    @SerializedName("surface_rating")
//    private float surfaceRating;  //: 0,
//
//    @SerializedName("rating_crowds")
//    private float crowdsRating;   //: 0,
//
//    @SerializedName("rating_lights")
//    private float lightsRating;   //: 0,
//
//    @SerializedName("rating_management")
//    private float managementRating;  //: 0,
//
//    @SerializedName("matches_played_here")
//    private int matchesPlayed;    //:0,
//
//    @SerializedName("number_of_ratings")
//    private String ratingNotes;  //:"(0 rating(s) for this court)",
//
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
//    public String getPhone() {
//        return phone;
//    }
//
//    public void setPhone(String phone) {
//        this.phone = phone;
//    }
//
//    public String getWebsite() {
//        return website;
//    }
//
//    public void setWebsite(String website) {
//        this.website = website;
//    }
//
//    public String getAddress() {
//        return address;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }
//
//    public String getCity() {
//        return city;
//    }
//
//    public void setCity(String city) {
//        this.city = city;
//    }
//
//    public String getState() {
//        return state;
//    }
//
//    public void setState(String state) {
//        this.state = state;
//    }
//
//    public String getZipCode() {
//        return zipCode;
//    }
//
//    public void setZipCode(String zipCode) {
//        this.zipCode = zipCode;
//    }
//
//    public double getLatitude() {
//        return latitude;
//    }
//
//    public void setLatitude(double latitude) {
//        this.latitude = latitude;
//    }
//
//    public double getLongitude() {
//        return longitude;
//    }
//
//    public void setLongitude(double longitude) {
//        this.longitude = longitude;
//    }
//
//    public String getNotes() {
//        return notes;
//    }
//
//    public void setNotes(String notes) {
//        this.notes = notes;
//    }
//
//    public long getLocationId() {
//        return locationId;
//    }
//
//    public void setLocationId(long locationId) {
//        this.locationId = locationId;
//    }
//
//    public long getRegionId() {
//        return regionId;
//    }
//
//    public void setRegionId(long regionId) {
//        this.regionId = regionId;
//    }
//
//    public int getIsClub() {
//        return isClub;
//    }
//
//    public void setIsClub(int isClub) {
//        this.isClub = isClub;
//    }
//
//    public int getHasStore() {
//        return hasStore;
//    }
//
//    public void setHasStore(int hasStore) {
//        this.hasStore = hasStore;
//    }
//
//    public int getHasStringer() {
//        return hasStringer;
//    }
//
//    public void setHasStringer(int hasStringer) {
//        this.hasStringer = hasStringer;
//    }
//
//    public int getTotalCourts() {
//        return totalCourts;
//    }
//
//    public void setTotalCourts(int totalCourts) {
//        this.totalCourts = totalCourts;
//    }
//
//    public int getIndoorCourts() {
//        return indoorCourts;
//    }
//
//    public void setIndoorCourts(int indoorCourts) {
//        this.indoorCourts = indoorCourts;
//    }
//
//    public int getClayCourts() {
//        return clayCourts;
//    }
//
//    public void setClayCourts(int clayCourts) {
//        this.clayCourts = clayCourts;
//    }
//
//    public int getLightedCourts() {
//        return lightedCourts;
//    }
//
//    public void setLightedCourts(int lightedCourts) {
//        this.lightedCourts = lightedCourts;
//    }
//
//    public int getHasHittingWall() {
//        return hasHittingWall;
//    }
//
//    public void setHasHittingWall(int hasHittingWall) {
//        this.hasHittingWall = hasHittingWall;
//    }
//
//    public int getRestricted() {
//        return restricted;
//    }
//
//    public void setRestricted(int restricted) {
//        this.restricted = restricted;
//    }
//
//    public int getHasBathroom() {
//        return hasBathroom;
//    }
//
//    public void setHasBathroom(int hasBathroom) {
//        this.hasBathroom = hasBathroom;
//    }
//
//    public int getHasWater() {
//        return hasWater;
//    }
//
//    public void setHasWater(int hasWater) {
//        this.hasWater = hasWater;
//    }
//
//    public int getHasFee() {
//        return hasFee;
//    }
//
//    public void setHasFee(int hasFee) {
//        this.hasFee = hasFee;
//    }
//
//    public float getFee() {
//        return fee;
//    }
//
//    public void setFee(float fee) {
//        this.fee = fee;
//    }
//
//    public float getOverallRating() {
//        return overallRating;
//    }
//
//    public void setOverallRating(float overallRating) {
//        this.overallRating = overallRating;
//    }
//
//    public float getSurfaceRating() {
//        return surfaceRating;
//    }
//
//    public void setSurfaceRating(float surfaceRating) {
//        this.surfaceRating = surfaceRating;
//    }
//
//    public float getCrowdsRating() {
//        return crowdsRating;
//    }
//
//    public void setCrowdsRating(float crowdsRating) {
//        this.crowdsRating = crowdsRating;
//    }
//
//    public float getLightsRating() {
//        return lightsRating;
//    }
//
//    public void setLightsRating(float lightsRating) {
//        this.lightsRating = lightsRating;
//    }
//
//    public float getManagementRating() {
//        return managementRating;
//    }
//
//    public void setManagementRating(float managementRating) {
//        this.managementRating = managementRating;
//    }
//
//    public String getRatingNotes() {
//        return ratingNotes;
//    }
//
//    public void setRatingNotes(String ratingNotes) {
//        this.ratingNotes = ratingNotes;
//    }
//
//    public int getMatchesPlayed() {
//        return matchesPlayed;
//    }
//
//    public void setMatchesPlayed(int matchesPlayed) {
//        this.matchesPlayed = matchesPlayed;
//    }
//
//}
//
//
////package com.tennisdc.tln.model;
////
////import com.google.gson.annotations.SerializedName;
////import com.tennisdc.tln.model.potyresponse.CommentListParcelConverter;
////
////import org.parceler.Parcel;
////import org.parceler.ParcelPropertyConverter;
////
////import java.util.List;
////
////import io.realm.RealmList;
////import io.realm.RealmObject;
////import io.realm.annotations.PrimaryKey;
////import io.realm.annotations.RealmClass;
////
/////**
//// * Created  on 2015-03-12.
//// */
////@RealmClass
////@Parcel(value = Parcel.Serialization.BEAN, analyze = {Court.class})
////public class Court extends RealmObject {
////
////    @PrimaryKey
////    @SerializedName("id")
////    private long id;
////
////    @SerializedName("location_id")
////    private String locationId; //:"74",
////
////    @SerializedName("court_region_id")
////    private String regionId; //:"146",
////
////    @SerializedName("name")
////    private String name;
////
////   @SerializedName("phone_number")
////    private String phone;    //:"8786354563",
////
////    @SerializedName("website")
////    private String website;  //:"rc.com",
////
////  @SerializedName("address")
////    private String address; //: "340 W Alexander Blvd., Elmhurst IL  60126",
////
////   @SerializedName("city")
////    private String city; //:"city",
////
////   @SerializedName("state")
////    private String state;    //:"state",
////
////    @SerializedName("zip_code")
////    private String zipCode;  //:"10011",
////
////   @SerializedName("latitude") //: "41.8978442",
////    private double latitude;
////
////  @SerializedName("longitude")    //: "-87.9509467"
////    private double longitude;
////
////   @SerializedName("notes")
////    private String notes;    //:"notes"
////
////   @SerializedName("is_club")
////    private int isClub; //:"1",
////
////    @SerializedName("has_store")
////    private int hasStore; //:"1",
////
////   @SerializedName("has_stringer")
////    private int hasStringer; //:"1",
////
////    @SerializedName("total_courts")
////    private int totalCourts; //:"1",
////
////  @SerializedName("indoor_courts")
////    private int indoorCourts;    //:"1",
////
////   @SerializedName("clay_courts")
////    private int clayCourts;     //:"1",
////
////   @SerializedName("lighted_courts")
////    private int lightedCourts;   //:"2",
////
////    @SerializedName("has_hitting_wall")
////    private int hasHittingWall;  //:"1",
////
////    @SerializedName("restricted")
////    private int restricted;  //:"1",
////
////   @SerializedName("bathroom_available")
////    private int hasBathroom; //:"1",
////
////  @SerializedName("water_access")
////    private int hasWater;    //:"1",
////
////    @SerializedName("has_fee")
////    private int hasFee;  //:"1",
////
////    @SerializedName("fee")
////    private float fee;   //:"2",
////
////   @SerializedName("overall_rating")
////    private float overallRating; //: 0,
////
////    @SerializedName("surface_rating")
////    private float surfaceRating;  //: 0,
////
////    @SerializedName("rating_crowds")
////    private float crowdsRating;   //: 0,
////
////    @SerializedName("rating_lights")
////    private float lightsRating;   //: 0,
////
////   @SerializedName("rating_management")
////    private float managementRating;  //: 0,
////
////    @SerializedName("matches_played_here")
////    private int matchesPlayed;    //:0,
////
////   @SerializedName("number_of_ratings")
////    private String ratingNotes;
////    //:"(0 rating(s) for this court)",
////
/////*    //    private RealmList<Comment> comments = new RealmList<Comment>();
//////    @SerializedName("comments")
//////    private RealmList<Comment> comments;
////    @SerializedName("comments")
////    @ParcelPropertyConverter(CommentListParcelConverter.class)
////    private RealmList<Comment> comments;
////
////    public Court() {
////    }
////
////    public RealmList<Comment> getComments() {
////        return comments;
////    }
////
////    public void setComments(RealmList<Comment> comments) {
////        this.comments = comments;
////    }*/
////
////    public long getId() {
////        return id;
////    }
////
////    public void setId(l id) {
////        this.id = id;
////    }
////
////    public String getName() {
////        return name;
////    }
////
////    public void setName(String name) {
////        this.name = name;
////    }
////
////    public String getPhone() {
////        return phone;
////    }
////
////    public void setPhone(String phone) {
////        this.phone = phone;
////    }
////
////    public String getWebsite() {
////        return website;
////    }
////
////    public void setWebsite(String website) {
////        this.website = website;
////    }
////
////    public String getAddress() {
////        return address;
////    }
////
////    public void setAddress(String address) {
////        this.address = address;
////    }
////
////    public String getCity() {
////        return city;
////    }
////
////    public void setCity(String city) {
////        this.city = city;
////    }
////
////    public String getState() {
////        return state;
////    }
////
////    public void setState(String state) {
////        this.state = state;
////    }
////
////    public String getZipCode() {
////        return zipCode;
////    }
////
////    public void setZipCode(String zipCode) {
////        this.zipCode = zipCode;
////    }
////
////    public double getLatitude() {
////        return latitude;
////    }
////
////    public void setLatitude(double latitude) {
////        this.latitude = latitude;
////    }
////
////    public double getLongitude() {
////        return longitude;
////    }
////
////    public void setLongitude(double longitude) {
////        this.longitude = longitude;
////    }
////
////    public String getNotes() {
////        return notes;
////    }
////
////    public void setNotes(String notes) {
////        this.notes = notes;
////    }
////
////    public String getLocationId() {
////        return locationId;
////    }
////
////    public void setLocationId(String locationId) {
////        this.locationId = locationId;
////    }
////
////    public String getRegionId() {
////        return regionId;
////    }
////
////    public void setRegionId(String regionId) {
////        this.regionId = regionId;
////    }
////
////    public int getIsClub() {
////        return isClub;
////    }
////
////    public void setIsClub(int isClub) {
////        this.isClub = isClub;
////    }
////
////    public int getHasStore() {
////        return hasStore;
////    }
////
////    public void setHasStore(int hasStore) {
////        this.hasStore = hasStore;
////    }
////
////    public int getHasStringer() {
////        return hasStringer;
////    }
////
////    public void setHasStringer(int hasStringer) {
////        this.hasStringer = hasStringer;
////    }
////
////    public int getTotalCourts() {
////        return totalCourts;
////    }
////
////    public void setTotalCourts(int totalCourts) {
////        this.totalCourts = totalCourts;
////    }
////
////    public int getIndoorCourts() {
////        return indoorCourts;
////    }
////
////    public void setIndoorCourts(int indoorCourts) {
////        this.indoorCourts = indoorCourts;
////    }
////
////    public int getClayCourts() {
////        return clayCourts;
////    }
////
////    public void setClayCourts(int clayCourts) {
////        this.clayCourts = clayCourts;
////    }
////
////    public int getLightedCourts() {
////        return lightedCourts;
////    }
////
////    public void setLightedCourts(int lightedCourts) {
////        this.lightedCourts = lightedCourts;
////    }
////
////    public int getHasHittingWall() {
////        return hasHittingWall;
////    }
////
////    public void setHasHittingWall(int hasHittingWall) {
////        this.hasHittingWall = hasHittingWall;
////    }
////
////    public int getRestricted() {
////        return restricted;
////    }
////
////    public void setRestricted(int restricted) {
////        this.restricted = restricted;
////    }
////
////    public int getHasBathroom() {
////        return hasBathroom;
////    }
////
////    public void setHasBathroom(int hasBathroom) {
////        this.hasBathroom = hasBathroom;
////    }
////
////    public int getHasWater() {
////        return hasWater;
////    }
////
////    public void setHasWater(int hasWater) {
////        this.hasWater = hasWater;
////    }
////
////    public int getHasFee() {
////        return hasFee;
////    }
////
////    public void setHasFee(int hasFee) {
////        this.hasFee = hasFee;
////    }
////
////    public float getFee() {
////        return fee;
////    }
////
////    public void setFee(float fee) {
////        this.fee = fee;
////    }
////
////    public float getOverallRating() {
////        return overallRating;
////    }
////
////    public void setOverallRating(float overallRating) {
////        this.overallRating = overallRating;
////    }
////
////    public float getSurfaceRating() {
////        return surfaceRating;
////    }
////
////    public void setSurfaceRating(float surfaceRating) {
////        this.surfaceRating = surfaceRating;
////    }
////
////    public float getCrowdsRating() {
////        return crowdsRating;
////    }
////
////    public void setCrowdsRating(float crowdsRating) {
////        this.crowdsRating = crowdsRating;
////    }
////
////    public float getLightsRating() {
////        return lightsRating;
////    }
////
////    public void setLightsRating(float lightsRating) {
////        this.lightsRating = lightsRating;
////    }
////
////    public float getManagementRating() {
////        return managementRating;
////    }
////
////    public void setManagementRating(float managementRating) {
////        this.managementRating = managementRating;
////    }
////
////    public String getRatingNotes() {
////        return ratingNotes;
////    }
////
////    public void setRatingNotes(String ratingNotes) {
////        this.ratingNotes = ratingNotes;
////    }
////
////    public int getMatchesPlayed() {
////        return matchesPlayed;
////    }
////
////    public void setMatchesPlayed(int matchesPlayed) {
////        this.matchesPlayed = matchesPlayed;
////    }
////
////
////
////
//////    public Comment getComments() {
//////        return comments;
//////    }
//////
//////    public void setComments(Comment comments) {
//////        this.comments = comments;
//////    }
////
////}
