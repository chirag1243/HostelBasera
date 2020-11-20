package com.hostelbasera.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Chirag on 27/10/20.
 */
public class DocumentUploadModel implements Serializable {

    @SerializedName("uploadPropertyImagesDetail")
    public ArrayList<UploadPropertyImagesDetail> uploadPropertyImagesDetail;
    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;
    @SerializedName("is_valid_token")
    public boolean is_valid_token;

    public static class UploadPropertyImagesDetail implements Serializable {
        @SerializedName("file_id")
        public int file_id;
        @SerializedName("image_size_str")
        public String image_size_str;
        @SerializedName("image_type")
        public String image_type;
        @SerializedName("image_height")
        public String image_height;
        @SerializedName("image_width")
        public String image_width;
        @SerializedName("is_image")
        public boolean is_image;
        @SerializedName("file_size")
        public String file_size;
        @SerializedName("file_ext")
        public String file_ext;
        @SerializedName("client_name")
        public String client_name;
        @SerializedName("orig_name")
        public String orig_name;
        @SerializedName("raw_name")
        public String raw_name;
        @SerializedName("full_path")
        public String full_path;
        @SerializedName("file_path")
        public String file_path;
        @SerializedName("file_type")
        public String file_type;
        @SerializedName("file_name")
        public String file_name;
    }
}
