package com.hostelbasera.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CityDetailModel implements Serializable {

    @SerializedName("cityDetail")
    public ArrayList<CityDetail> cityDetail;
    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;
    @SerializedName("is_valid_token")
    public boolean is_valid_token;

    public static class CityDetail implements Serializable {
        @SerializedName("city_id")
        public int city_id;
        @SerializedName("is_popular")
        public boolean is_popular;
        @SerializedName("img")
        public String img;
        @SerializedName("city_name")
        public String city_name;
    }
}
