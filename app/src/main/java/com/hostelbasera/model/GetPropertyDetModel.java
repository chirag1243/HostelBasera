package com.hostelbasera.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetPropertyDetModel implements Serializable {

    @SerializedName("propertyDetails")
    public PropertyDetails propertyDetails;
    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;
    @SerializedName("is_valid_token")
    public boolean is_valid_token;

    public static class PropertyDetails implements Serializable {
        @SerializedName("propertyrooms")
        public ArrayList<Propertyrooms> propertyrooms;
        @SerializedName("propertyFacility")
        public ArrayList<Integer> propertyFacility;
        @SerializedName("productImages")
        public ArrayList<String> productImages;
        @SerializedName("price")
        public String price;
        @SerializedName("cooking_menu")
        public String cooking_menu;
        @SerializedName("laundry_fees")
        public String laundry_fees;
        @SerializedName("water_timing")
        public String water_timing;
        @SerializedName("timing")
        public String timing;
        @SerializedName("city_id")
        public int city_id;
        @SerializedName("state_id")
        public int state_id;
        @SerializedName("description")
        public String description;
        @SerializedName("cont_no")
        public String cont_no;
        @SerializedName("latitude")
        public String latitude;
        @SerializedName("longitude")
        public String longitude;
        @SerializedName("address")
        public String address;
        @SerializedName("email")
        public String email;
        @SerializedName("property_size_id")
        public int property_size_id;
        @SerializedName("property_category_id")
        public int property_category_id;
        @SerializedName("property_name")
        public String property_name;
        @SerializedName("seller_id")
        public int seller_id;
        @SerializedName("type_id")
        public int type_id;
    }

    public static class Propertyrooms implements Serializable {
        @SerializedName("roomprice")
        public int roomprice;
        @SerializedName("roomname")
        public String roomname;
    }
}
