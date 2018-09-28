package com.hostelbasera.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SellerDropdownModel implements Serializable {

    @SerializedName("sellerDropdownDetail")
    public SellerDropdownDetail sellerDropdownDetail;
    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public int status;
    @SerializedName("is_valid_token")
    public boolean is_valid_token;

    public static class SellerDropdownDetail implements Serializable {
        @SerializedName("facilityList")
        public List<FacilityList> facilityList;
        @SerializedName("stateList")
        public List<StateList> stateList;
        @SerializedName("propertysizes")
        public List<Propertysizes> propertysizes;
        @SerializedName("propertytypesList")
        public List<PropertytypesList> propertytypesList;
        @SerializedName("propertycategoriesList")
        public List<PropertycategoriesList> propertycategoriesList;
        @SerializedName("typesList")
        public List<TypesList> typesList;
    }

    public static class FacilityList implements Serializable {
        @SerializedName("flag")
        public int flag;
        @SerializedName("facility_icon_img")
        public String facility_icon_img;
        @SerializedName("facility_name")
        public String facility_name;
        @SerializedName("facility_id")
        public int facility_id;
    }

    public static class StateList implements Serializable {
        @SerializedName("flag")
        public int flag;
        @SerializedName("state_name")
        public String state_name;
        @SerializedName("state_id")
        public int state_id;
    }

    public static class Propertysizes implements Serializable {
        @SerializedName("property_size")
        public String property_size;
        @SerializedName("property_size_id")
        public int property_size_id;
    }

    public static class PropertytypesList implements Serializable {
        @SerializedName("property_type")
        public String property_type;
        @SerializedName("property_type_id")
        public int property_type_id;
    }

    public static class PropertycategoriesList implements Serializable {
        @SerializedName("property_category_name")
        public String property_category_name;
        @SerializedName("property_category_id")
        public int property_category_id;
    }

    public static class TypesList implements Serializable {
        @SerializedName("type_name")
        public String type_name;
        @SerializedName("type_id")
        public int type_id;
    }
}
