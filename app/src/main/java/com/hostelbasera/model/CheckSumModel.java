package com.hostelbasera.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CheckSumModel implements Serializable {

    @SerializedName("payt_STATUS")
    public String payt_STATUS;
    @SerializedName("ORDER_ID")
    public String ORDER_ID;
    @SerializedName("CHECKSUMHASH")
    public String CHECKSUMHASH;
}
