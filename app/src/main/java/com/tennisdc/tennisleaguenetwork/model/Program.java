package com.tennisdc.tennisleaguenetwork.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created  on 2015-04-16.
 */
@Parcel
public class Program {

    @SerializedName("id")
    public long id;

    @SerializedName("name")
    public String name;

    @SerializedName("type")
    public String type;

    @SerializedName("dates")
    public String dates;

    @SerializedName("free_month")
    public boolean freeMonth;

    @SerializedName("already_joined")
    public boolean alreadyJoined;

    @SerializedName("price")
    public List<ProgramPrice> programPrices;

    @Expose(serialize = false, deserialize = false)
    public int selectedPriceIndex;

    @Parcel
    public static class ProgramPrice {
        @SerializedName("id")
        public long id;

        @SerializedName("description")
        public String description;

        @SerializedName("amount")
        public Double price;

        @SerializedName("price_after_discount")
        public Double priceAfterDiscount;

        @SerializedName("discount_message")
        public String discountMessage;

        @Override
        public String toString() {
            return this.description;
        }
    }

}
