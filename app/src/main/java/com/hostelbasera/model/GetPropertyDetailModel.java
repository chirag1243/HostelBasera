package com.hostelbasera.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GetPropertyDetailModel implements Serializable {

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
        @SerializedName("property_type")
        public String property_type;
        @SerializedName("property_category_name")
        public String property_category_name;
        @SerializedName("image")
        public String image;
        @SerializedName("flag")
        public String flag;
        @SerializedName("date")
        public String date;
        @SerializedName("by_admin")
        public String by_admin;
        @SerializedName("seller_id")
        public String seller_id;
        @SerializedName("cooking_menu")
        public String cooking_menu;
        @SerializedName("water_timing")
        public String water_timing;
        @SerializedName("laundry_fees")
        public String laundry_fees;
        @SerializedName("timing")
        public String timing;
        @SerializedName("profile_pic")
        public String profile_pic;
        @SerializedName("longitude")
        public String longitude;
        @SerializedName("latitude")
        public String latitude;
        @SerializedName("state_id")
        public String state_id;
        @SerializedName("city_id")
        public String city_id;
        @SerializedName("email")
        public String email;
        @SerializedName("cont_no")
        public String cont_no;
        @SerializedName("description")
        public String description;
        @SerializedName("address")
        public String address;
        @SerializedName("property_size_id")
        public String property_size_id;
        @SerializedName("type_id")
        public String type_id;
        @SerializedName("property_type_id")
        public String property_type_id;
        @SerializedName("property_category_id")
        public String property_category_id;
        @SerializedName("property_name")
        public String property_name;
        @SerializedName("property_id")
        public int property_id;

        @SerializedName("price")
        public String price;

        @SerializedName("city_name")
        public String city_name;
    }
}
