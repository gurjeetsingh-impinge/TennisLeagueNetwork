package com.tennisdc.tennisleaguenetwork.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created  on 2015-04-17.
 */
public class CouponDetails {

    @SerializedName("id")
    public long id;

    @SerializedName("discount")
    public double discount;

    @SerializedName("type")
    public String type;

    @SerializedName("message")
    public String message;

    @Expose(deserialize = false, serialize = false)
    public String promoCode;

    public double calculateDiscount(double totalAmount) {
        return (type.equals("%") ? (0.01 * discount) * totalAmount : discount);
    }

}
