package com.hostelbasera.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserDetailModel implements Serializable {

    @SerializedName("loginUserDetail")
    public LoginUserDetail loginUserDetail;
    @SerializedName("loginSellerDetail")
    public LoginSellerDetail loginSellerDetail;
    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;

    @SerializedName("is_exiting")
    public int is_exiting;

    public static class LoginUserDetail implements Serializable {
        @SerializedName("token")
        public String token;
        @SerializedName("contact_no")
        public String contact_no;
        @SerializedName("email")
        public String email;
        @SerializedName("name")
        public String name;
        @SerializedName("user_reg_Id")
        public int user_reg_Id;

        @SerializedName("password")
        public String password;
    }

    public static class LoginSellerDetail implements Serializable {
        @SerializedName("token")
        public String token;
        @SerializedName("contact_no")
        public String contact_no;
        @SerializedName("email")
        public String email;
        @SerializedName("name")
        public String name;
        @SerializedName("seller_reg_Id")
        public int seller_reg_Id;

        @SerializedName("password")
        public String password;
    }
}
