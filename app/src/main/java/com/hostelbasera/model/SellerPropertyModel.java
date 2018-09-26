package com.hostelbasera.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class SellerPropertyModel implements Serializable {

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

    public static class PropertyDetail {
        @SerializedName("rating")
        public int rating;
        @SerializedName("city_name")
        public String city_name;
        @SerializedName("property_type")
        public String property_type;
        @SerializedName("property_category_name")
        public String property_category_name;
        @SerializedName("image")
        public String image;
        @SerializedName("flag")
        public int flag;
        @SerializedName("date")
        public String date;
        @SerializedName("by_admin")
        public int by_admin;
        @SerializedName("seller_id")
        public int seller_id;
        @SerializedName("price")
        public String price;
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
        public int state_id;
        @SerializedName("city_id")
        public int city_id;
        @SerializedName("email")
        public String email;
        @SerializedName("cont_no")
        public String cont_no;
        @SerializedName("description")
        public String description;
        @SerializedName("address")
        public String address;
        @SerializedName("property_size_id")
        public int property_size_id;
        @SerializedName("type_id")
        public int type_id;
        @SerializedName("property_type_id")
        public int property_type_id;
        @SerializedName("property_category_id")
        public int property_category_id;
        @SerializedName("property_name")
        public String property_name;
        @SerializedName("property_id")
        public int property_id;
    }
}
