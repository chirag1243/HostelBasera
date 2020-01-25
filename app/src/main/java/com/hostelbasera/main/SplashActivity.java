package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.gson.Gson;
import com.hostelbasera.R;
import com.hostelbasera.apis.HttpRequestHandler;
import com.hostelbasera.apis.PostRequest;
import com.hostelbasera.model.UserDetailModel;
import com.hostelbasera.seller.SellerDashboardActivity;
import com.hostelbasera.utility.AppSignatureHelper;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.Globals;
import com.hostelbasera.utility.Toaster;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashActivity extends AppCompatActivity {

    Globals globals;
    @BindView(R.id.rotate_loading)
    RotateLoading rotateLoading;
    @BindView(R.id.btn_retry)
    Button btnRetry;
    @BindView(R.id.fl_rotate_loading)
    FrameLayout flRotateLoading;
    @BindView(R.id.tv_powered_by)
    TextView tvPoweredBy;
    //    @BindView(R.id.img_icon)
//    ImageView imgIcon;
    boolean isSeller;
    UserDetailModel userModel;
    private static final int UpdateCode = 2212;

    String property_name = "";
    int property_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        globals = ((Globals) getApplicationContext());
        ButterKnife.bind(this);

        AppSignatureHelper appSignatureHelper = new AppSignatureHelper(this);
        appSignatureHelper.getAppSignatures();

//        Toaster.shortToast("Hash String is  " + appSignatureHelper.getAppSignatures().get(0));
        globals.setHasKey(appSignatureHelper.getAppSignatures().get(0));

        tvPoweredBy.setTypeface(tvPoweredBy.getTypeface(), Typeface.BOLD);
        isSeller = globals.getIsSeller();
        userModel = globals.getUserDetails();

        try {
            Intent intent = getIntent();
            Uri data = intent.getData();

            if (data != null && data.getPath() != null) {
                property_id = Integer.parseInt(data.getPath().substring(data.getPath().lastIndexOf("/") + 1));
                property_name = data.getPath().substring(data.getPath().indexOf("w/") + 2, data.getPath().lastIndexOf("/"))
                        .replaceAll("-", " ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        init();
    }

    private void init() {
        if (globals.getUserDetails() != null) {
            if (Globals.isNetworkAvailable(this)) {
                btnRetry.setVisibility(View.INVISIBLE);
                startLoader();
                doLogin();
            } else {
                btnRetry.setVisibility(View.VISIBLE);
                stopLoader();
                Toaster.shortToast(R.string.no_internet_msg);
            }
        } else {
            redirect();
        }
    }

    @SuppressLint("HardwareIds")
    public void doLogin() {
        String version = "1.1.8";
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        JSONObject postData = HttpRequestHandler.getInstance().getLoginUserParam(Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID),
                isSeller ? userModel.loginSellerDetail.email : userModel.loginUserDetail.email,
                isSeller ? userModel.loginSellerDetail.password : userModel.loginUserDetail.password, isSeller, version);

        if (postData != null) {
            new PostRequest(this, isSeller ? getString(R.string.loginSeller) : getString(R.string.loginUser),
                    postData, false, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    UserDetailModel userDetailModel = new Gson().fromJson(response.toString(), UserDetailModel.class);
                    if (userDetailModel.status == 0) {
                        if (isSeller) {
                            userDetailModel.loginSellerDetail.password = userModel.loginSellerDetail.password;
                            globals.setNewUserId(userDetailModel.loginSellerDetail.seller_reg_Id);
                        } else {
                            userDetailModel.loginUserDetail.password = userModel.loginUserDetail.password;
                            globals.setNewUserId(userDetailModel.loginUserDetail.user_reg_Id);
                        }
                        globals.setIsSeller(isSeller);
                        globals.setUserDetails(userDetailModel);

                        updateChecker();

                    } else {
                        Toaster.shortToast(userDetailModel.message);
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    }
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
    }

    public void updateChecker() {
        UserDetailModel.VersionDetail versionDetail = isSeller ? globals.getUserDetails().loginSellerDetail.versionDetail : globals.getUserDetails().loginUserDetail.versionDetail;
        if (versionDetail.is_update_available) {
            MaterialStyledDialog.Builder builder = new MaterialStyledDialog.Builder(this);
            builder.setTitle(R.string.new_update_available)
                    .setDescription("Update ver." + versionDetail.latest_version + " is available to download. Downloading the latest update you will get the latest features, " + versionDetail.remark + " of HostelBasera.")
                    .setCancelable(false)
                    .setIcon(R.mipmap.ic_launcher)
                    .setHeaderDrawable(R.drawable.nav_bg)
                    .autoDismiss(false)
                    .withDarkerOverlay(false)
                    .setPositiveText("Update")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            final String appPackageName = getPackageName();
                            try {
                                startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)), UpdateCode);
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)), UpdateCode);
                            }
                            dialog.dismiss();
                        }
                    });
            if (!versionDetail.force_update) {
                builder.setNegativeText("Later")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                redirectDashboard();
                                dialog.dismiss();
                            }
                        });
            }
            builder.show();
        } else {
            redirectDashboard();
        }
    }

    public void redirectDashboard() {
        startActivity(new Intent(SplashActivity.this, isSeller ? SellerDashboardActivity.class : DashboardActivity.class)
                .putExtra(Constant.Property_id, property_id)
                .putExtra(Constant.Property_name, property_name));
        finish();
    }

    private void redirect() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }, 2000);
    }

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
        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);

        if (requestCode == UpdateCode) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    updateChecker();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
