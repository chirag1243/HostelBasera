package com.hostelbasera.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Chirag on 10/11/20.
 */
public class OrderListModel implements Serializable {

    @SerializedName("order_list")
    public ArrayList<Order_list> order_list;
    @SerializedName("total_orders")
    public int total_orders;
    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;

    public static class Order_list implements Serializable {
        @SerializedName("created_at")
        public String created_at;
        @SerializedName("property_name")
        public String property_name;
        @SerializedName("seller_name")
        public String seller_name;
        @SerializedName("user_name")
        public String user_name;
        @SerializedName("payment_status")
        public String payment_status;
        @SerializedName("remark")
        public String remark;
        @SerializedName("applied_wallet_amount")
        public int applied_wallet_amount;
        @SerializedName("applied_coupon_amount")
        public int applied_coupon_amount;
        @SerializedName("coupon_amout_type")
        public String coupon_amout_type;
        @SerializedName("coupon_id")
        public String coupon_id;
        @SerializedName("property_id")
        public String property_id;
        @SerializedName("seller_id")
        public String seller_id;
        @SerializedName("user_id")
        public String user_id;
        @SerializedName("id")
        public String id;

        @SerializedName("start_date")
        public String start_date;
        @SerializedName("end_date")
        public String end_date;

        @SerializedName("room_name")
        public String room_name;
        @SerializedName("room_price")
        public int room_price;

        @SerializedName("user_property_request_id")
        public String user_property_request_id;
        @SerializedName("user_documents")
        public ArrayList<String> user_documents;

    }
}
