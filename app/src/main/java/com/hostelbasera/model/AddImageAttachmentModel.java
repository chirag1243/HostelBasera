package com.hostelbasera.model;

import com.google.gson.annotations.SerializedName;

public class AddImageAttachmentModel {

    @SerializedName("FilePath")
    public String FilePath;
    @SerializedName("FileType")
    public String FileType;

    @SerializedName("FileName")
    public String FileName;
}
