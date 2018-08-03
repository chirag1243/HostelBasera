package com.hostelbasera.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserDetailModel implements Serializable {

    @SerializedName("loginUserDetail")
    private LoginUserDetail loginUserDetail;
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private int status;

    public static class LoginUserDetail implements Serializable {
        @SerializedName("token")
        private String token;
        @SerializedName("contact_no")
        private String contact_no;
        @SerializedName("email")
        private String email;
        @SerializedName("name")
        private String name;
        @SerializedName("user_reg_Id")
        private int user_reg_Id;
    }
}
