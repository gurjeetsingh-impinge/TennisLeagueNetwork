package com.tennisdc.tennisleaguenetwork.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created on 08-Sep-17.
 */

public class Discount {

    @SerializedName("name")
    public String Name;

    @SerializedName("description")
    public String Description;

    @SerializedName("percentage")
    public int Percentage;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getPercentage() {
        return Percentage;
    }

    public void setPercentage(int percentage) {
        Percentage = percentage;
    }
}
