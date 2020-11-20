package com.hostelbasera.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Chirag on 10/11/20.
 */
public class BannerListModel implements Serializable {

    @SerializedName("banners")
    public ArrayList<Banners> banners;
    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;

    public static class Banners implements Serializable {
        @SerializedName("updated_at")
        public String updated_at;
        @SerializedName("created_at")
        public String created_at;
        @SerializedName("is_deleted")
        public String is_deleted;
        @SerializedName("is_active")
        public String is_active;
        @SerializedName("url")
        public String url;
        @SerializedName("image")
        public String image;
        @SerializedName("title")
        public String title;
        @SerializedName("id")
        public String id;
    }
}
