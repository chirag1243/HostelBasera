package com.hostelbasera.apis;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;

import com.hostelbasera.R;
import com.hostelbasera.main.LoginActivity;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.Globals;
import com.hostelbasera.utility.Toaster;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.cloudist.acplibrary.ACProgressFlower;
import cz.msebera.android.httpclient.Header;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;

public class PostRequest {

    private OnPostServiceCallListener listener;
    private OnNoInternetListener internetListener;
    private JSONObject postData;
    private Context context;
    private String url;
    private boolean isLoaderRequired, isDialog = false;
    private ACProgressFlower dialog = null;
    private ProgressBar pb = null;
    private Globals globals;

    public interface OnPostServiceCallListener {
        void onSucceedToPostCall(JSONObject response);

        void onFailedToPostCall(int statusCode, String msg);
    }

    public interface OnNoInternetListener {
        void onNoInternet(String msg);
    }

    public PostRequest(Context context, String url, JSONObject postData, boolean isLoaderRequired, OnPostServiceCallListener listener) {
        this.listener = listener;
        this.postData = postData;
        this.context = context;
        this.isLoaderRequired = isLoaderRequired;
        this.url = context.getString(R.string.server_url) + url;
    }

    public PostRequest(Context context, String server_url, String url, JSONObject postData, boolean isLoaderRequired, OnPostServiceCallListener listener) {
        this.listener = listener;
        this.postData = postData;
        this.context = context;
        this.isLoaderRequired = isLoaderRequired;
        this.url = server_url + url;
    }

    public PostRequest(Context context, String url, JSONObject postData, boolean isLoaderRequired, OnPostServiceCallListener listener, OnNoInternetListener internetListener) {
        this.listener = listener;
        this.postData = postData;
        this.context = context;
        this.isLoaderRequired = isLoaderRequired;
        this.url = url;
        this.internetListener = internetListener;
    }

    public PostRequest(Context context, String url, JSONObject postData, boolean isLoaderRequired, boolean isDialog, OnPostServiceCallListener listener, OnNoInternetListener internetListener) {
        this.listener = listener;
        this.internetListener = internetListener;
        this.postData = postData;
        this.context = context;
        this.isLoaderRequired = isLoaderRequired;
        this.url = url;
        this.isDialog = isDialog;
    }

    public PostRequest(Context context, String url, JSONObject postData, ProgressBar pb, boolean isLoaderRequired, OnPostServiceCallListener listener) {
        this.listener = listener;
        this.postData = postData;
        this.context = context;
        this.url = url;
        this.pb = pb;
        this.isLoaderRequired = isLoaderRequired;
    }

    public PostRequest(Context context, String url, JSONObject postData, ProgressBar pb, boolean isLoaderRequired, boolean isDialog, OnPostServiceCallListener listener, OnNoInternetListener internetListener) {
        this.listener = listener;
        this.postData = postData;
        this.context = context;
        this.url = url;
        this.pb = pb;
        this.isLoaderRequired = isLoaderRequired;
        this.internetListener = internetListener;
        this.isDialog = isDialog;
    }

    public void execute() {
        globals = ((Globals) context.getApplicationContext());

        if (!Globals.isNetworkAvailable(context)) {
            if (!isDialog && internetListener != null)
                internetListener.onNoInternet(context.getString(R.string.no_internet_msg));
            return;
        }

        if (isLoaderRequired) { //&& pb == null
            dialog = HttpRequestHandler.getInstance().getProgressBar(context);
        }
        Logger.d("URL :=>", url);
        Logger.json(postData.toString());
        HttpRequestHandler.getInstance().post(context, url, postData, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                if (dialog != null && !dialog.isShowing()) {
                    dialog.show();
                } else {
                    if (pb != null && isLoaderRequired) {
                        pb.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
                else {
                    if (pb != null && isLoaderRequired) {
                        pb.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Logger.json(response.toString());
                try {
                    if (response.has(Constant.Is_valid_token) && !response.getBoolean(Constant.Is_valid_token)) {
                        Toaster.longToast(response.getString(Constant.Message));
                        globals.setUserDetails(null);
                        globals.setNewUserId(0);
                        context.startActivity(new Intent(context, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (listener != null) {
                    listener.onSucceedToPostCall(response);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                try {
                    if (listener != null) {
                        if (statusCode == HTTP_BAD_REQUEST) {
                            if (errorResponse.has(Constant.Message) && errorResponse.has(Constant.Status)) {
                                String msg = errorResponse.getString(Constant.Message);
                                if (msg != null && !msg.isEmpty())
                                    listener.onFailedToPostCall(statusCode, msg);
                                else
                                    listener.onFailedToPostCall(statusCode, context.getString(R.string.msg_server_error));
                            } else {
                                listener.onFailedToPostCall(statusCode, context.getString(R.string.msg_server_error));
                            }
                        } else {
                            listener.onFailedToPostCall(statusCode, context.getString(R.string.msg_server_error));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (listener != null)
                    listener.onFailedToPostCall(statusCode, context.getString(R.string.error_msg_connection_timeout));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                if (listener != null)
                    listener.onFailedToPostCall(statusCode, context.getString(R.string.error_msg_connection_timeout));
            }
        });
    }
}
