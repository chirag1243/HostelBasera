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

        @SerializedName("versionDetail")
        public VersionDetail versionDetail;
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

        @SerializedName("versionDetail")
        public VersionDetail versionDetail;
    }

    public static class VersionDetail implements Serializable {
        @SerializedName("remark")
        public String remark;
        @SerializedName("latest_version")
        public String latest_version;
        @SerializedName("is_update_available")
        public boolean is_update_available;
        @SerializedName("force_update")
        public boolean force_update;
    }
}
