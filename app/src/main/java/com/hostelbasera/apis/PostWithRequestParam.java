package com.hostelbasera.apis;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import com.hostelbasera.R;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.Globals;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cc.cloudist.acplibrary.ACProgressFlower;
import cz.msebera.android.httpclient.Header;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;

public class PostWithRequestParam {

    private OnPostWithReqParamServiceCallListener listener;
    private RequestParams postData;
    private Context context;
    private String url;
    private boolean isLoaderRequired;
    private ACProgressFlower dialog = null;
    private ProgressBar pb = null;

    public interface OnPostWithReqParamServiceCallListener {
        void onSucceedToPostCall(JSONObject response);

        void onFailedToPostCall(int statusCode, String msg);

        void onProgressCall(int progress);
    }

    public PostWithRequestParam(Context context, String url, RequestParams postData, boolean isLoaderRequired, OnPostWithReqParamServiceCallListener listener) {
        this.listener = listener;
        this.postData = postData;
        this.context = context;
        this.isLoaderRequired = isLoaderRequired;
        this.url = context.getString(R.string.server_url) + url;
        Logger.d("URL" + url);
    }

    public void execute() {
        if (!Globals.isNetworkAvailable(context))
            return;

        if (isLoaderRequired) {
            dialog = HttpRequestHandler.getInstance().getProgressBar(context);
        }

        HttpRequestHandler.getInstance().postWithReqestParam(url, postData, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                if (dialog != null) {
                    dialog.show();
                } else {
                    if (pb != null) {
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
                    if (pb != null) {
                        pb.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                try {
                    listener.onProgressCall(Math.round((bytesWritten/totalSize)*100));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Logger.json(response.toString());
                if (listener != null)
                    listener.onSucceedToPostCall(response);
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
