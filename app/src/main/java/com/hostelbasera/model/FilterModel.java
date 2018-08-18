package com.hostelbasera.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FilterModel implements Serializable {

    @SerializedName("filterDetail")
    public ArrayList<FilterDetail> filterDetail;
    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;
    @SerializedName("is_valid_token")
    public boolean is_valid_token;

    public static class FilterDetail implements Serializable {
        @SerializedName("propertySize")
        public ArrayList<PropertySize> propertySize;
        @SerializedName("name")
        public String name;
        @SerializedName("isSelected")
        public boolean isSelected;
    }

    public static class PropertySize implements Serializable {
        @SerializedName("property_size")
        public String property_size;
        @SerializedName("property_size_id")
        public int property_size_id;

        @SerializedName("isSelected")
        public boolean isSelected;
    }
}
