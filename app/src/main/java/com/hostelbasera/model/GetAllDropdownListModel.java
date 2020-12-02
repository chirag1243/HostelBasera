package com.hostelbasera.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetAllDropdownListModel implements Serializable {

    @SerializedName("allDropdownListDetail")
    public AllDropdownListDetail allDropdownListDetail;
    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;
    @SerializedName("is_valid_token")
    public boolean is_valid_token;

    public static class AllDropdownListDetail implements Serializable {
        @SerializedName("city_list")
        public ArrayList<City_list> city_list;
    }

    public static class City_list implements Serializable {
        @SerializedName("flag")
        public int flag;
        @SerializedName("is_popular")
        public int is_popular;
        @SerializedName("state_id")
        public int state_id;
        @SerializedName("img")
        public String img;
        @SerializedName("city_name")
        public String city_name;
        @SerializedName("city_id")
        public int city_id;

        @NonNull
        @Override
        public String toString() {
            return city_name;
        }
    }
}
