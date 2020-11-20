package com.hostelbasera.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Chirag on 29/10/20.
 */
public class BookingListDataModel implements Serializable {

    @SerializedName("booking_requests")
    public ArrayList<Booking_requests> booking_requests;
    @SerializedName("total_booking_requests")
    public int total_booking_requests;
    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;

    public static class Booking_requests implements Serializable {
        @SerializedName("room_price")
        public String room_price;

        @SerializedName("created_at")
        public String created_at;
        @SerializedName("end_date")
        public String end_date;
        @SerializedName("start_date")
        public String start_date;
        @SerializedName("status")
        public int status;
        @SerializedName("room_name")
        public String room_name;
        @SerializedName("property_name")
        public String property_name;
        @SerializedName("seller_name")
        public String seller_name;
        @SerializedName("user_name")
        public String user_name;
        @SerializedName("property_id")
        public int property_id;
        @SerializedName("seller_id")
        public int seller_id;
        @SerializedName("user_id")
        public String user_id;
        @SerializedName("id")
        public int id;

        @SerializedName("user_documents")
        public ArrayList<String> user_documents;

    }
}
