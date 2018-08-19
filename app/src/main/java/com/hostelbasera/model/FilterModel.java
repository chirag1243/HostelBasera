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
        @SerializedName("data")
        public ArrayList<Data> data;
        @SerializedName("name")
        public String name;
        @SerializedName("filterType")
        public int filterType;

        @SerializedName("isSelected")
        public boolean isSelected;
    }

    public static class Data implements Serializable {
        @SerializedName("property_size")
        public String property_size;
        @SerializedName("property_size_id")
        public int property_size_id;

        @SerializedName("property_type")
        public String property_type;
        @SerializedName("property_type_id")
        public int property_type_id;

        @SerializedName("type_name")
        public String type_name;
        @SerializedName("type_id")
        public int type_id;

        @SerializedName("isSelected")
        public boolean isSelected;
    }

}
