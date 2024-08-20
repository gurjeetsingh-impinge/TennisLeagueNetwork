package com.tennisdc.tennisleaguenetwork.model;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created  on 02-02-2015.
 */
@Parcel
public class AlertDetail {

    @SerializedName("id")
    public long id;

    @SerializedName("title")
    public String title;

    @SerializedName("description")
    public String tagDesc;

    @SerializedName("description_without_tags")
    public String descriptions;

    @SerializedName("slide_url")
    public String slider_image_url;

    @SerializedName("thumb_shade_url")
    public String thumb_shade_url;

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return descriptions;
    }

    public String getImageUrl() {
        return thumb_shade_url;
    }

    public String getBackImageUrl() {
        return slider_image_url;
    }
}