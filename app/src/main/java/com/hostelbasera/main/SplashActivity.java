package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.hostelbasera.R;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.Globals;
import com.hostelbasera.utility.Toaster;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.protocol.HttpRequestHandler;

public class SplashActivity extends Activity {

    Globals globals;
    @BindView(R.id.rotate_loading)
    RotateLoading rotateLoading;
    @BindView(R.id.btn_retry)
    Button btnRetry;
    @BindView(R.id.fl_rotate_loading)
    FrameLayout flRotateLoading;
//    @BindView(R.id.img_icon)
//    ImageView imgIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        globals = ((Globals) getApplicationContext());
        ButterKnife.bind(this);
        Logger.addLogAdapter(new AndroidLogAdapter());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }, 3000);
//        init();
    }

   /* private void init() {
        if (globals.getUserDetails() != null) {
            if (Globals.isNetworkAvailable(this)) {
                btnRetry.setVisibility(View.INVISIBLE);
                startLoader();
                doLogin();
            } else {
                btnRetry.setVisibility(View.VISIBLE);
                Toaster.shortToast(R.string.no_internet_msg);
            }
        } else {
            redirect();
        }
    }*/

   /* @SuppressLint("HardwareIds")
    public void doLogin() {
        JSONObject postData = HttpRequestHandler.getInstance().getLoginUserParam(Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID), globals.getUserDetails().loginUserDetail.mobile, globals.getUserDetails().loginUserDetail.password);

        if (postData != null) {
            new PostRequest(this, getString(R.string.loginUser), postData, false, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    UserDetailModel userDetailModel = new Gson().fromJson(response.toString(), UserDetailModel.class);
                    if (userDetailModel.status == 0) {
                        userDetailModel.loginUserDetail.password = globals.getUserDetails().loginUserDetail.password;


                        globals.setUserDetails(userDetailModel);
                        startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
                    } else {
                        Toaster.shortToast(userDetailModel.message);
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    }
                    finish();
                }

                @Override
                public void onFailedToPostCall(int statusCode, String msg) {
                    stopLoader();
                    btnRetry.setVisibility(View.VISIBLE);
                    Toaster.shortToast(msg);
                }
            }).execute();
        }
        Globals.hideKeyboard(this);
    }*/

    /*private void redirect() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }, 2000);
    }*/

    public void stopLoader() {
        rotateLoading.stop();
        rotateLoading.setVisibility(View.INVISIBLE);
        flRotateLoading.setVisibility(View.INVISIBLE);
    }

    public void startLoader() {
        flRotateLoading.setVisibility(View.VISIBLE);
        rotateLoading.setVisibility(View.VISIBLE);
        rotateLoading.start();
//        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        rotate.setDuration(10000);
//        rotate.setInterpolator(new LinearInterpolator());
//
//        rotate.setRepeatCount(Animation.INFINITE);
//        imgIcon.startAnimation(rotate);
    }

    @OnClick(R.id.btn_retry)
    public void onViewClicked() {
//        init();
    }
}
