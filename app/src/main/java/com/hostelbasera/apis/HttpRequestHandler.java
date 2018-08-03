package com.hostelbasera.apis;

import android.content.Context;
import android.graphics.Color;

import com.hostelbasera.model.UserDetailModel;
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


    public JSONObject getLoginUserParam(String deviceId, String mobile, String password) {
        JSONObject params = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            params.put(Constant.DeviceType, Constant.AndroidDeviceType);
            params.put(Constant.DeviceId, deviceId);

            jsonObject.put(Constant.Mobile, mobile);
            jsonObject.put(Constant.Password, password);
            jsonObject.put(Constant.Fcm_token, globals.getFCMDeviceToken());

            params.put(Constant.LoginUserData, jsonObject);
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



    /*private JSONObject mParams;

    public JSONObject setDefaultParameters() {
        try {
            mParams = new JSONObject();
            mParams.put(Constant.Token, globals.getUserDetails().loginUserDetail.token);
            mParams.put(Constant.DeviceType, Constant.AndroidDeviceType);
            mParams.put(Constant.User_id, globals.getUserDetails().loginUserDetail.user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mParams;
    }*/


}

