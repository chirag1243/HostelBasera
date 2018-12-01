package com.hostelbasera.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CheckSellerPaymentDataModel implements Serializable {

    @SerializedName("payment_value")
    public int payment_value;
    @SerializedName("priceBlockDetails")
    public ArrayList<PriceBlockDetails> priceBlockDetails;
    @SerializedName("payment_required")
    public int payment_required;
    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;

    public static class PriceBlockDetails implements Serializable {
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
