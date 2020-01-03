package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hostelbasera.R;
import com.hostelbasera.apis.HttpRequestHandler;
import com.hostelbasera.apis.PostRequest;
import com.hostelbasera.model.UserDetailModel;
import com.hostelbasera.seller.SellerDashboardActivity;
import com.hostelbasera.utility.BaseActivity;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.Globals;
import com.hostelbasera.utility.OtpEditText;
import com.hostelbasera.utility.Toaster;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OTPVerifyActivity extends BaseActivity {

    @BindView(R.id.tv_otp)
    TextView tvOtp;
    @BindView(R.id.edt_otp)
    OtpEditText edtOtp;
    @BindView(R.id.btn_verify)
    Button btnVerify;

    String mobile_no = "";
    boolean isSeller;
    Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverify);
        ButterKnife.bind(this);
        globals = ((Globals) getApplicationContext());

        mobile_no = getIntent().getStringExtra(Constant.Mobile_no);
        isSeller = getIntent().getBooleanExtra(Constant.IsSeller, false);
    }

    @OnClick(R.id.btn_verify)
    public void onViewClicked() {
        if (!Globals.isNetworkAvailable(OTPVerifyActivity.this)) {
            Toaster.shortToast(getString(R.string.no_internet_msg));
            return;
        }

        if (edtOtp.getText().toString().trim().isEmpty()) {
            Toaster.shortToast("Please enter otp.");
            return;
        } else if (edtOtp.getText().toString().length() != 6) {
            Toaster.shortToast("Please enter otp.");
            return;
        }
        doLogin();
    }

    String version = "1.1.7";

    @SuppressLint("HardwareIds")
    public void doLogin() {

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        JSONObject postData = HttpRequestHandler.getInstance().getLoginUserParam(Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID), mobile_no, edtOtp.getText().toString(), isSeller, version);

        if (postData != null) {
            new PostRequest(this, isSeller ? getString(R.string.loginSeller) : getString(R.string.loginUser), postData, true,
                    new PostRequest.OnPostServiceCallListener() {
                        @Override
                        public void onSucceedToPostCall(JSONObject response) {
                            UserDetailModel userDetailModel = new Gson().fromJson(response.toString(), UserDetailModel.class);
                            if (userDetailModel.status == 0) {
                                if (isSeller) {
                                    userDetailModel.loginSellerDetail.password = edtOtp.getText().toString();
                                    globals.setNewUserId(userDetailModel.loginSellerDetail.seller_reg_Id);
                                } else {
                                    userDetailModel.loginUserDetail.password = edtOtp.getText().toString();
                                    globals.setNewUserId(userDetailModel.loginUserDetail.user_reg_Id);
                                }
                                globals.setIsSeller(isSeller);
                                globals.setUserDetails(userDetailModel);
                                startActivity(new Intent(OTPVerifyActivity.this, isSeller ? SellerDashboardActivity.class : DashboardActivity.class));
                                finish();
                            }
                            Toaster.shortToast(userDetailModel.message);
                        }

                        @Override
                        public void onFailedToPostCall(int statusCode, String msg) {
                            Toaster.shortToast(msg);
                        }
                    }).execute();
        }
        Globals.hideKeyboard(this);
    }
}
