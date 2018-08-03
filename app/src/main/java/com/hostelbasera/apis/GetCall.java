package com.hostelbasera.apis;

import android.content.Context;

import com.hostelbasera.utility.Globals;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cc.cloudist.acplibrary.ACProgressFlower;
import cz.msebera.android.httpclient.Header;

/**
 * Created by chirag on 18/06/18.
 */

public class GetCall {

    private OnGetServiceCallListener listener;
    private JSONObject postData;
    private Context context;
    private String url;

    public interface OnGetServiceCallListener {
        public void onSucceedToGetCall(JSONObject response);

        public void onFailedToGetCall();
    }

    public GetCall(Context context, String url, JSONObject postData, OnGetServiceCallListener listener) {
        this.listener = listener;
        this.postData = postData;
        this.context = context;
        this.url = url;
    }

    public void doRequest() {
        if (!Globals.isNetworkAvailable(context))
            return;

        final ACProgressFlower dialog = HttpRequestHandler.getInstance().getProgressBar(context);
        HttpRequestHandler.getInstance().get(url, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                dialog.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                listener.onSucceedToGetCall(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                listener.onFailedToGetCall();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                listener.onFailedToGetCall();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                listener.onFailedToGetCall();
            }
        });
    }
}
