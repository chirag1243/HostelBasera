package com.hostelbasera.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class PriceBlockDetailModel implements Serializable {

    @SerializedName("priceBlockDetail")
    public ArrayList<PriceBlockDetail> priceBlockDetail;
    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;
    @SerializedName("is_valid_token")
    public boolean is_valid_token;

    public static class PriceBlockDetail implements Serializable {
        @SerializedName("date")
        public String date;
        @SerializedName("flag")
        public int flag;
        @SerializedName("total_number_of_hostel")
        public int total_number_of_hostel;
        @SerializedName("price")
        public int price;
        @SerializedName("discription")
        public String discription;
        @SerializedName("title")
        public String title;
        @SerializedName("id")
        public int id;
    }
}
