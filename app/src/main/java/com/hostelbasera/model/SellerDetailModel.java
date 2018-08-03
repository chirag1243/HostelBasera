package com.hostelbasera.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SellerDetailModel implements Serializable {

    @SerializedName("loginSellerDetail")
    public LoginSellerDetail loginSellerDetail;
    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;

    public static class LoginSellerDetail {
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
    }
}
