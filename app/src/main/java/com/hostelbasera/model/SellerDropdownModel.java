package com.hostelbasera.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
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
        public ArrayList<FacilityList> facilityList;
        @SerializedName("stateList")
        public ArrayList<StateList> stateList;
        @SerializedName("propertysizes")
        public ArrayList<Propertysizes> propertysizes;
        @SerializedName("propertytypesList")
        public ArrayList<PropertytypesList> propertytypesList;
        @SerializedName("propertycategoriesList")
        public ArrayList<PropertycategoriesList> propertycategoriesList;
        @SerializedName("typesList")
        public ArrayList<TypesList> typesList;
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

        public boolean isSelected;

        @Override
        public String toString() {
            return facility_name;
        }
    }

    public static class StateList implements Serializable {
        @SerializedName("cityList")
        public ArrayList<CityList> cityList;
        @SerializedName("flag")
        public int flag;
        @SerializedName("state_name")
        public String state_name;
        @SerializedName("state_id")
        public int state_id;

        @Override
        public String toString() {
            return state_name;
        }
    }

    public static class CityList implements Serializable {
        @SerializedName("flag")
        public int flag;
        @SerializedName("state_id")
        public int state_id;
        @SerializedName("city_name")
        public String city_name;
        @SerializedName("city_id")
        public int city_id;

        @Override
        public String toString() {
            return city_name;
        }
    }

    public static class Propertysizes implements Serializable {
        @SerializedName("property_size")
        public String property_size;
        @SerializedName("property_size_id")
        public int property_size_id;

        @Override
        public String toString() {
            return property_size;
        }
    }

    public static class PropertytypesList implements Serializable {
        @SerializedName("property_type")
        public String property_type;
        @SerializedName("property_type_id")
        public int property_type_id;

        @Override
        public String toString() {
            return property_type;
        }
    }

    public static class PropertycategoriesList implements Serializable {
        @SerializedName("property_category_name")
        public String property_category_name;
        @SerializedName("property_category_id")
        public int property_category_id;

        @Override
        public String toString() {
            return property_category_name;
        }
    }

    public static class TypesList implements Serializable {
        @SerializedName("type_name")
        public String type_name;
        @SerializedName("type_id")
        public int type_id;

        @Override
        public String toString() {
            return type_name;
        }
    }
}
