package com.tennisdc.tln.model;

import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;


/**
 * Created  on 02-02-2015.
 */
@Parcel
public class Competition implements Parcelable {

    public static final Creator<Competition> CREATOR = new Creator<Competition>() {
        @Override
        public Competition createFromParcel(android.os.Parcel in) {
            return new Competition(in);
        }

        @Override
        public Competition[] newArray(int size) {
            return new Competition[size];
        }
    };
    @SerializedName("competition_name")
    public String CompetitionName;
    @SerializedName("competition_id")
    public long CompetitionId;
    @SerializedName("division_id")
    public long DivisionId;
    @SerializedName("division_name")
    public String DivisionName;
    @SerializedName("is_doubles")
    public boolean is_doubles;
    @SerializedName("start_date")
    public String start_date;
    @SerializedName("enrolled")
    public boolean enrolled;
    @SerializedName("closed")
    public boolean closed;
    @SerializedName("status")
    public int status;
    @SerializedName("description")
    public String description;
    @SerializedName("type")
    public String type;
    @SerializedName("color_code")
    public String color_code;
    public CompetitionType CompitionType;
    public List<DivisionBean> divisions;
    public Competition() {
    }

    public String getColor_code() {
        return color_code;
    }

    public void setColor_code(String color_code) {
        this.color_code = color_code;
    }

    public List<DivisionBean> getDivisions() {
        return divisions;
    }

    public void setDivisions(List<DivisionBean> divisions) {
        this.divisions = divisions;
    }

    public String getCompetitionName() {
        return CompetitionName;
    }

    public void setCompetitionName(String competitionName) {
        CompetitionName = competitionName;
    }

    public long getCompetitionId() {
        return CompetitionId;
    }

    public void setCompetitionId(long competitionId) {
        CompetitionId = competitionId;
    }

    public long getDivisionId() {
        return DivisionId;
    }

    public void setDivisionId(long divisionId) {
        DivisionId = divisionId;
    }

    public String getDivisionName() {
        return DivisionName;
    }

    public void setDivisionName(String divisionName) {
        DivisionName = divisionName;
    }

    public boolean isIs_doubles() {
        return is_doubles;
    }

    public void setIs_doubles(boolean is_doubles) {
        this.is_doubles = is_doubles;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public boolean isEnrolled() {
        return enrolled;
    }

    public void setEnrolled(boolean enrolled) {
        this.enrolled = enrolled;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public int isStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    protected Competition(android.os.Parcel in) {
        CompetitionName = in.readString();
        CompetitionId = in.readLong();
        DivisionId = in.readLong();
        DivisionName = in.readString();
        is_doubles = (Boolean)in.readValue(null );
        start_date = in.readString();
        enrolled = (Boolean)in.readValue(null );
        closed = (Boolean)in.readValue(null );
        status = in.readInt();
        description = in.readString();
        type = in.readString();
        color_code = in.readString();
    }

    @Override
    public String toString() {
        return CompetitionName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel parcel, int i) {
        parcel.writeString(CompetitionName);
        parcel.writeLong(CompetitionId);
        parcel.writeLong(DivisionId);
        parcel.writeString(DivisionName);
        parcel.writeValue(is_doubles);
        parcel.writeString(start_date);
        parcel.writeValue(enrolled);
        parcel.writeValue(closed);
        parcel.writeValue(status);
        parcel.writeString(description);
        parcel.writeString(type);
        parcel.writeString(color_code);
    }}

