package com.hostelbasera.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class PropertyDetailModel implements Serializable {

    @SerializedName("propertyDetails")
    public PropertyDetails propertyDetails;
    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;
    @SerializedName("is_valid_token")
    public boolean is_valid_token;

    public static class PropertyDetails implements Serializable{
        @SerializedName("propertyFacility")
        public ArrayList<PropertyFacility> propertyFacility;
        @SerializedName("productImages")
        public ArrayList<String> productImages;
        @SerializedName("city_name")
        public String city_name;
        @SerializedName("flag")
        public String flag;
        @SerializedName("date")
        public String date;
        @SerializedName("by_admin")
        public String by_admin;
        @SerializedName("seller_id")
        public String seller_id;
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
        public int property_category_id;
        @SerializedName("property_name")
        public String property_name;
        @SerializedName("property_id")
        public int property_id;
        @SerializedName("property_size")
        public String property_size;
        @SerializedName("type_name")
        public String type_name;
        @SerializedName("property_type")
        public String property_type;
        @SerializedName("property_category_name")
        public String property_category_name;

        @SerializedName("propertyReviewdetails")
        public ArrayList<PropertyReviewDetails> propertyReviewDetails;

        @SerializedName("isBookMark")
        public boolean isBookMark;

        @SerializedName("rating")
        public int rating;
    }

    public static class PropertyReviewDetails implements Serializable{
        @SerializedName("review")
        public String review;
        @SerializedName("date")
        public String date;
        @SerializedName("user_name")
        public String user_name;
        @SerializedName("rating")
        public float rating;

    }

    public static class PropertyFacility  implements Serializable{
        @SerializedName("facility_icon_img")
        public String facility_icon_img;
        @SerializedName("facility_name")
        public String facility_name;
    }
}
