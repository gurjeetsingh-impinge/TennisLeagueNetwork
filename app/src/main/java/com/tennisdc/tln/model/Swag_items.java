package com.tennisdc.tln.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created  on 2015-04-16.
 */
@Parcel
public class Swag_items {

    @SerializedName("id")
    public long id;

    @SerializedName("title")
    public String title;

    @SerializedName("cost")
    public String cost;

    @SerializedName("shipping_cost")
    public String shipping_cost;

    @SerializedName("description")
    public String description;

    @SerializedName("image")
    public String image;

    @SerializedName("sizes")
    public List<String> sizes;


}
