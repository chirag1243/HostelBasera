package com.hostelbasera.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Chirag on 05/11/20.
 */
public class UserWalletBalanceModel implements Serializable {

    @SerializedName("user_wallet_money")
    public User_wallet_money user_wallet_money;
    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;

    public static class User_wallet_money implements Serializable {
        @SerializedName("updated_at")
        public String updated_at;
        @SerializedName("created_at")
        public String created_at;
        @SerializedName("is_deleted")
        public String is_deleted;
        @SerializedName("amount")
        public String amount;
        @SerializedName("user_id")
        public String user_id;
        @SerializedName("id")
        public String id;
    }
}
