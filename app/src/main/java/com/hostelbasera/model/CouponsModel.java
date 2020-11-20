package com.hostelbasera.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Chirag on 05/11/20.
 */
public class CouponsModel implements Serializable {

    @SerializedName("coupons")
    public ArrayList<Coupons> coupons;
    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;

    public static class Coupons implements Serializable {
        @SerializedName("updated_at")
        public String updated_at;
        @SerializedName("created_at")
        public String created_at;
        @SerializedName("is_deleted")
        public String is_deleted;
        @SerializedName("category_id")
        public String category_id;
        @SerializedName("max_price")
        public String max_price;
        @SerializedName("min_price")
        public String min_price;
        @SerializedName("status")
        public String status;
        @SerializedName("expiry_date")
        public String expiry_date;
        @SerializedName("amount")
        public int amount;
        @SerializedName("amount_type")
        public int amount_type;
        @SerializedName("code")
        public String code;
        @SerializedName("id")
        public int id;

        public boolean isApplied;
    }
}
