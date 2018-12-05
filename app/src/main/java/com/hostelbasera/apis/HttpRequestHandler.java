package com.hostelbasera.apis;

import android.content.Context;
import android.graphics.Color;

import com.hostelbasera.model.AddImageAttachmentModel;
import com.hostelbasera.model.AddRoomModel;
import com.hostelbasera.model.SellerDropdownModel;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.Globals;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cc.cloudist.acplibrary.ACProgressPie;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by chirag on 18/06/18.
 */

public class HttpRequestHandler {
    private Globals globals = (Globals) Globals.getContext();
    private static final String HEADER_TYPE_JSON = "application/json";

    private static HttpRequestHandler mInstance = null;
    private final AsyncHttpClient client;
    public String TAG = getClass().getName();


    private HttpRequestHandler() {
        client = new AsyncHttpClient();
        client.setTimeout(30000);
    }

    public static HttpRequestHandler getInstance() {
        if (mInstance == null) {
            mInstance = new HttpRequestHandler();

        }
        return mInstance;
    }

    public void get(String url, AsyncHttpResponseHandler responseHandler) {
        client.get(url, responseHandler);
    }

    void post(Context context, String url, JSONObject params, AsyncHttpResponseHandler responseHandler) {

        try {
            Logger.e("Server Url:=> ", url);
            Logger.json(params.toString());
            StringEntity entity = new StringEntity(params.toString(), "UTF-8");
            client.post(context, url, entity, HEADER_TYPE_JSON, responseHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void postWithReqestParam(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        Logger.e("Server Url:=> ", url);
        Logger.json(params.toString());
        client.post(url, params, responseHandler);
    }

    public ACProgressFlower getProgressBar(Context context) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(context)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .petalThickness(5)
                .themeColor(Color.WHITE)
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        return dialog;
    }

    public ACProgressFlower getProgressBarWithText(Context context, String msg) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(context)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .petalThickness(5)
                .themeColor(Color.WHITE)
                .text(msg)
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        return dialog;
    }

    public ACProgressPie getProgressPieWithText(Context context, String msg) {
        ACProgressPie dialog = new ACProgressPie.Builder(context)
                .ringColor(Color.WHITE)
                .pieColor(Color.WHITE)
                .updateType(ACProgressConstant.PIE_AUTO_UPDATE)
                .build();
        dialog.show();

        return dialog;
    }

    public JSONObject getLoginUserParam(String deviceId, String email, String password, boolean isSeller, String version) {
        JSONObject params = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            params.put(Constant.DeviceType, Constant.AndroidDeviceType);
            params.put(Constant.DeviceId, deviceId);

            jsonObject.put(Constant.Email, email);
            jsonObject.put(Constant.Password, password);
            jsonObject.put(Constant.Fcm_token, globals.getFCMDeviceToken());
            jsonObject.put(Constant.Version, version);

            if (isSeller)
                params.put(Constant.LoginSellerData, jsonObject);
            else
                params.put(Constant.LoginUserData, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    public JSONObject getRegisterUserParam(String deviceId, String name, String password, String email, String mobile_no, String address, boolean isSeller, String fb_id, String google_id) {
        JSONObject params = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            params.put(Constant.DeviceType, Constant.AndroidDeviceType);
            params.put(Constant.DeviceId, deviceId);
            params.put(Constant.Fcm_token, globals.getFCMDeviceToken());

            jsonObject.put(isSeller ? Constant.Seller_name : Constant.User_name, name);
            jsonObject.put(isSeller ? Constant.Seller_pass : Constant.User_pass, password);
            jsonObject.put(isSeller ? Constant.Seller_email : Constant.User_email, email);
            jsonObject.put(isSeller ? Constant.Seller_cont_no : Constant.User_cont_no, mobile_no);
            jsonObject.put(isSeller ? Constant.Seller_address : Constant.User_address, address);

            jsonObject.put(Constant.Fb_id, fb_id);
            jsonObject.put(Constant.Google_id, google_id);

            params.put(isSeller ? Constant.RegisterSellerData : Constant.RegisterUserData, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }


    public JSONObject getPropertyListDataParam(int pageNo, ArrayList<String> arrPropertyCategoryId, ArrayList<String> arrPropertyTypeId, ArrayList<String> arrTypeId, ArrayList<String> arrPropertySizeId) {
        JSONObject params = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            params = setDefaultParameters();

            JSONObject jsonFilters = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < arrPropertyCategoryId.size(); i++) {
                jsonArray.put(arrPropertyCategoryId.get(i));
            }
            jsonFilters.put(Constant.Property_category_id, jsonArray);

            jsonArray = new JSONArray();
            for (int i = 0; i < arrPropertyTypeId.size(); i++) {
                jsonArray.put(arrPropertyTypeId.get(i));
            }
            jsonFilters.put(Constant.Property_type_id, jsonArray);

            jsonArray = new JSONArray();
            for (int i = 0; i < arrTypeId.size(); i++) {
                jsonArray.put(arrTypeId.get(i));
            }
            jsonFilters.put(Constant.Type_id, jsonArray);

            jsonArray = new JSONArray();
            for (int i = 0; i < arrPropertySizeId.size(); i++) {
                jsonArray.put(arrPropertySizeId.get(i));
            }
            jsonFilters.put(Constant.Property_size_id, jsonArray);

            jsonObject.put(Constant.Filters, jsonFilters);
            jsonObject.put(Constant.Limit, 0);
            jsonObject.put(Constant.Page, pageNo);

            params.put(Constant.GetPropertyListData, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    public JSONObject getNearbyPropertyDataParam(int pageNo, ArrayList<String> arrPropertyCategoryId, ArrayList<String> arrPropertyTypeId, ArrayList<String> arrTypeId, ArrayList<String> arrPropertySizeId, double latitude, double longitude) {
        JSONObject params = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            params = setDefaultParameters();

            JSONObject jsonFilters = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < arrPropertyCategoryId.size(); i++) {
                jsonArray.put(arrPropertyCategoryId.get(i));
            }
            jsonFilters.put(Constant.Property_category_id, jsonArray);

            jsonArray = new JSONArray();
            for (int i = 0; i < arrPropertyTypeId.size(); i++) {
                jsonArray.put(arrPropertyTypeId.get(i));
            }
            jsonFilters.put(Constant.Property_type_id, jsonArray);

            jsonArray = new JSONArray();
            for (int i = 0; i < arrTypeId.size(); i++) {
                jsonArray.put(arrTypeId.get(i));
            }
            jsonFilters.put(Constant.Type_id, jsonArray);

            jsonArray = new JSONArray();
            for (int i = 0; i < arrPropertySizeId.size(); i++) {
                jsonArray.put(arrPropertySizeId.get(i));
            }
            jsonFilters.put(Constant.Property_size_id, jsonArray);

            jsonFilters.put(Constant.Latitude, "" + latitude);
            jsonFilters.put(Constant.Longitude, "" + longitude);
            jsonFilters.put(Constant.Distance, "0");

            jsonObject.put(Constant.Filters, jsonFilters);
            jsonObject.put(Constant.Limit, 0);
            jsonObject.put(Constant.Page, pageNo);

            params.put(Constant.GetNearbyPropertyData, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    public JSONObject getPropertyDetailsParam(int property_id) {
        JSONObject params = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            params = setDefaultParameters();

            jsonObject.put(Constant.Property_id, property_id);
            jsonObject.put(Constant.User_id, globals.getUserId());

            params.put(Constant.GetPropertyData, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    public JSONObject getAddBookmarkParam(int property_id, boolean is_remove) {
        JSONObject params = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            params = setDefaultParameters();

            jsonObject.put(Constant.Property_id, property_id);
            jsonObject.put(Constant.User_id, globals.getUserId());
            jsonObject.put(Constant.Is_remove, is_remove);

            params.put(Constant.AddBookmarkData, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }


    public JSONObject getFilterListParam() {
        JSONObject params = new JSONObject();
        try {
            params = setDefaultParameters();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    public JSONObject getAddReviewDataParam(int property_id, String review, int rating) {
        JSONObject params = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            params = setDefaultParameters();

            jsonObject.put(Constant.Property_id, property_id);
            jsonObject.put(Constant.Review, review);
            jsonObject.put(Constant.Rating, rating);
            jsonObject.put(Constant.User_id, globals.getUserId());

            params.put(Constant.AddReviewData, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    public JSONObject getAddInquiryDataParam(int property_id, String description) {
        JSONObject params = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            params = setDefaultParameters();

            jsonObject.put(Constant.Property_id, property_id);
            jsonObject.put(Constant.Description, description);
            jsonObject.put(Constant.User_id, globals.getUserId());

            params.put(Constant.AddInquiryData, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }


    public JSONObject getPropertyReviewDataParam(int property_id) {
        JSONObject params = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            params = setDefaultParameters();
            jsonObject.put(Constant.Property_id, property_id);
            params.put(Constant.GetPropertyReviewData, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }


    public JSONObject getAddOrderDataParam(int property_id, int room_id) {
        JSONObject params = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            params = setDefaultParameters();
            jsonObject.put(Constant.Property_id, property_id);
            jsonObject.put(Constant.Room_id, room_id);
            jsonObject.put(Constant.User_id, globals.getUserId());
            params.put(Constant.AddOrderData, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    public JSONObject getSearchDataParam(String search_text) {
        JSONObject params = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            params = setDefaultParameters();
            jsonObject.put(Constant.Searchtext, search_text);
            params.put(Constant.GetSearchData, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    public JSONObject getOrderDataParam() {
        JSONObject params = new JSONObject();
        try {
            params = setDefaultParameters();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    public JSONObject getSearchListDataParam(int page, String search_text) {
        JSONObject params = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            params = setDefaultParameters();
            jsonObject.put(Constant.Page, page);
            jsonObject.put(Constant.Limit, 0);
            jsonObject.put(Constant.Searchtext, search_text);
            params.put(Constant.GetSearchData, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    public JSONObject getAddFeedbackDataParam(String subject, String message) {
        JSONObject params = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            params = setDefaultParameters();
            jsonObject.put(Constant.Full_name, globals.getUserDetails().loginUserDetail.name);
            jsonObject.put(Constant.Email, globals.getUserDetails().loginUserDetail.email);
            jsonObject.put(Constant.Cont_no, globals.getUserDetails().loginUserDetail.contact_no);
            jsonObject.put(Constant.Subject, subject);
            jsonObject.put(Constant.Message, message);
            params.put(Constant.AddFeedbackData, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    public JSONObject getSellerPropertyListParam() {
        JSONObject params = new JSONObject();
        try {
            params = setDefaultParameters();
            params.put(Constant.Seller_id, globals.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    public JSONObject getSellerDropdownParam() {
        JSONObject params = new JSONObject();
        try {
            params = setDefaultParameters();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    /*
   {
    "token":"si0d3lRh4Of7ld03l",
    "deviceType":1,
    "user_id":2,

    "property_id":"",
    "addPropertyData":{
        "type_id":1,
        "seller_id":1,
        "property_name":"test property",
        "property_category_id":1,
        "property_size_id":2,
        "email":"test@gmail.com",
        "address":"501, empire state building,udhana darwaja,surat",
        "longitude":1.2565,
        "latitude":2.55558,
        "cont_no":9898976523,
        "description":"test",
        "state_id":2,
        "city_id":5,
        "timing":"",
        "water_timing":"",
        "laundry_fees":152,
        "cooking_menu":"1.jpg",
        "price":1582,
        "facility":[
                1,
                2,
                5
        ],
        "property_images":[
                            "1.jpg",
                            "2.jpg",
                            "5.jpg"
         ],
        "addPropertyRoom":[
                {
                    "roomname":"room1",
                    "roomprice":222
                },
                {
                    "roomname":"room1",
                    "roomprice":222
                }
        ]
    }
}
    */


    public JSONObject getAddPropertyParam(int property_id, int type_id, String property_name, int property_category_id,
                                          int property_size_id, String email, String address, double longitude,
                                          double latitude, String cont_no, String description, int state_id, int city_id,
                                          String timing, String water_timing, String laundry_fees, ArrayList<AddImageAttachmentModel> arrAddMenuAttachment,
                                          String price, ArrayList<SellerDropdownModel.FacilityList> arrFacilityList,
                                          ArrayList<AddImageAttachmentModel> arrAddImageAttachment, ArrayList<AddRoomModel> arrRoomDetails,
                                          String payment_id, String price_plan) {
        JSONObject params = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray;
        try {
            params = setDefaultParameters();
            if (property_id > 0) {
                params.put(Constant.Property_id, "" + property_id);
            }
            jsonObject.put(Constant.Type_id, type_id);
            jsonObject.put(Constant.Seller_id, globals.getUserId());
            jsonObject.put(Constant.Property_name, property_name);

            jsonObject.put(Constant.Property_category_id, property_category_id);
            jsonObject.put(Constant.Property_size_id, property_size_id);
            jsonObject.put(Constant.Email, email);
            jsonObject.put(Constant.Address, address);
            jsonObject.put(Constant.Longitude, longitude);
            jsonObject.put(Constant.Latitude, latitude);
            jsonObject.put(Constant.Cont_no, cont_no);
            jsonObject.put(Constant.Description, description);
            jsonObject.put(Constant.State_id, state_id);
            jsonObject.put(Constant.City_id, city_id);
            jsonObject.put(Constant.Timing, timing);
            jsonObject.put(Constant.Water_timing, water_timing);
            jsonObject.put(Constant.Laundry_fees, laundry_fees);

            jsonObject.put(Constant.Payment_id, payment_id);
            jsonObject.put(Constant.Price_plan, price_plan);


            StringBuilder cooking_menu = new StringBuilder();
            for (int i = 0; i < arrAddMenuAttachment.size(); i++) {
                cooking_menu.append(arrAddMenuAttachment.get(i).FileName).append(", ");
            }
            jsonObject.put(Constant.Cooking_menu, cooking_menu.deleteCharAt(cooking_menu.length() - 2));
            jsonObject.put(Constant.Price, price);

            jsonArray = new JSONArray();
            for (int i = 0; i < arrFacilityList.size(); i++) {
                if (arrFacilityList.get(i).isSelected) {
                    jsonArray.put(arrFacilityList.get(i).facility_id);
                }
            }
            jsonObject.put(Constant.Facility, jsonArray);

            jsonArray = new JSONArray();
            for (int i = 0; i < arrAddImageAttachment.size(); i++) {
                jsonArray.put(arrAddImageAttachment.get(i).FileName);
            }
            jsonObject.put(Constant.Property_images, jsonArray);

            jsonArray = new JSONArray();
            for (int i = 0; i < arrRoomDetails.size(); i++) {
                jsonArray.put(new JSONObject().put(Constant.Roomname, arrRoomDetails.get(i).name).put(Constant.Roomprice, arrRoomDetails.get(i).price));
            }
            jsonObject.put(Constant.AddPropertyRoom, jsonArray);

            params.put(Constant.AddPropertyData, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    public JSONObject getPriceBlockParam() {
        JSONObject params = new JSONObject();
        try {
            params = setDefaultParameters();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    /*
    {
	"token":"si0d3lRh4Of7ld03l",
	"deviceType":1,
    "seller_id":2
}


    */
    public JSONObject getSellerOrderDataParam() {
        JSONObject params = new JSONObject();
        try {
            params = setDefaultParameters();
            params.put(Constant.Seller_id, globals.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    /*
    {
	"token":"si0d3lRh4Of7ld03l",
	"deviceType":1,
    "user_id":2,
	"getPropertyData":{
			"property_id":17
		}
}

    */

    public JSONObject getPropertyDetParam(int property_id) {
        JSONObject params = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            params = setDefaultParameters();

            jsonObject.put(Constant.Property_id, property_id);

            params.put(Constant.GetPropertyData, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    public JSONObject getCheckExitingUserParam(String user_email, boolean isSeller) {
        JSONObject params = new JSONObject();
        try {

            params.put(isSeller ? Constant.Seller_email : Constant.User_email, user_email);

            params.put(Constant.Token, Constant.Token_Value);
            params.put(Constant.DeviceType, Constant.AndroidDeviceType);
            params.put(Constant.Fcm_token, globals.getFCMDeviceToken());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    public JSONObject getCheckSellerPaymentDataParam() {
        JSONObject params = new JSONObject();
        try {
            params.put(Constant.Token, Constant.Token_Value);
            params.put(Constant.DeviceType, Constant.AndroidDeviceType);
            params.put(Constant.Seller_id, globals.getUserDetails().loginSellerDetail.seller_reg_Id);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }


    public JSONObject getChangePasswordParam(boolean isSeller, String password) {
        JSONObject params = new JSONObject();
        try {
            params.put(Constant.Token, Constant.Token_Value);
            params.put(Constant.DeviceType, Constant.AndroidDeviceType);
            params.put(Constant.User_id, isSeller ? globals.getUserDetails().loginSellerDetail.seller_reg_Id : globals.getUserDetails().loginUserDetail.user_reg_Id);
            params.put(Constant.Is_seller, isSeller);
            params.put(Constant.Password, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    /*public JSONObject getLogoutUserParam() {
        JSONObject params = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            params = setDefaultParameters();

            jsonObject.put(Constant.User_id, globals.getUserDetails().loginUserDetail.user_id);
            jsonObject.put(Constant.Email, globals.getUserDetails().loginUserDetail.email);
            params.put(Constant.LogoutUserData, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }*/

    private JSONObject mParams;

    private JSONObject setDefaultParameters() {
        try {
            mParams = new JSONObject();
            //TODO : Change as per required
            mParams.put(Constant.Token, Constant.Token_Value);//globals.getUserDetails().loginUserDetail.token);
            mParams.put(Constant.DeviceType, Constant.AndroidDeviceType);
            mParams.put(Constant.User_id, globals.getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mParams;
    }


}

