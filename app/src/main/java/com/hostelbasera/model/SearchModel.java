package com.hostelbasera.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchModel implements Serializable {

    @SerializedName("propertyDetail")
    public ArrayList<PropertyDetail> propertyDetail;
    @SerializedName("total_properties")
    public int total_properties;
    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;
    @SerializedName("is_valid_token")
    public boolean is_valid_token;

    public static class PropertyDetail implements Serializable {
        @SerializedName("property_id")
        public int property_id;
        @SerializedName("property_name")
        public String property_name;
    }
}
