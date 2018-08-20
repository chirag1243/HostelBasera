package com.hostelbasera.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PropertyReviewModel implements Serializable {

    @SerializedName("propertyReviewDetails")
    public ArrayList<PropertyDetailModel.PropertyReviewDetails> propertyReviewDetails;
    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;
    @SerializedName("is_valid_token")
    public boolean is_valid_token;

}
