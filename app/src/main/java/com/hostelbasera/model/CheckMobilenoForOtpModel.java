package com.hostelbasera.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CheckMobilenoForOtpModel implements Serializable {

    @SerializedName("user_id")
    public int user_id;
    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;
}
