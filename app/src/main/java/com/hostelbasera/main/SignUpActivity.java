package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.AppCompatEditText;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.hostelbasera.R;
import com.hostelbasera.apis.HttpRequestHandler;
import com.hostelbasera.apis.PostRequest;
import com.hostelbasera.model.UserDetailModel;
import com.hostelbasera.seller.SellerDashboardActivity;
import com.hostelbasera.utility.BaseActivity;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.Globals;
import com.hostelbasera.utility.Toaster;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.hoang8f.android.segmented.SegmentedGroup;

public class SignUpActivity extends BaseActivity {

    @BindView(R.id.rb_buyer)
    RadioButton rbBuyer;
    @BindView(R.id.rb_seller)
    RadioButton rbSeller;
    @BindView(R.id.segmented_group)
    SegmentedGroup segmentedGroup;
    @BindView(R.id.edt_name)
    AppCompatEditText edtName;
    @BindView(R.id.edt_email)
    AppCompatEditText edtEmail;
    @BindView(R.id.edt_password)
    AppCompatEditText edtPassword;
    @BindView(R.id.edt_mobile_no)
    AppCompatEditText edtMobileNo;
    @BindView(R.id.edt_address)
    AppCompatEditText edtAddress;
    @BindView(R.id.btn_sign_up)
    Button btnSignUp;

    boolean isSeller;
    Globals globals;
    @BindView(R.id.ll_sign_up)
    LinearLayout llSignUp;
    Intent intent;
    String fb_id = "", google_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        init();
    }

    public void init() {
        globals = ((Globals) this.getApplicationContext());
        btnSignUp.setTypeface(btnSignUp.getTypeface(), Typeface.BOLD);

        intent = getIntent();
        if (intent != null) {
            edtEmail.setText(intent.getStringExtra(Constant.Email));
            edtName.setText(intent.getStringExtra(Constant.Full_name));
            fb_id = intent.getStringExtra(Constant.Fb_id);
            google_id = intent.getStringExtra(Constant.Google_id);
            if (intent.hasExtra(Constant.IsSeller)) {
                isSeller = intent.getBooleanExtra(Constant.IsSeller, false);
                if (isSeller) {
                    rbSeller.setChecked(true);
                } else {
                    rbBuyer.setChecked(true);
                }
            }
        }

        segmentedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                isSeller = checkedId != R.id.rb_buyer;
            }
        });
    }

    @OnClick(R.id.btn_sign_up)
    public void onSignUpClicked() {
        if (edtName.getText().toString().trim().isEmpty()) {
            Toaster.shortToast("Please enter name.");
            return;
        } else if (edtEmail.getText().toString().trim().isEmpty()) {
            Toaster.shortToast("Please enter email id.");
            return;
        } else if (edtPassword.getText().toString().trim().isEmpty()) {
            Toaster.shortToast("Please enter password.");
            return;
        } else if (edtPassword.getText().toString().length() < 6) {
            Toaster.shortToast("Password must be min 6 character.");
            return;
        } else if (edtMobileNo.getText().toString().trim().isEmpty()) {
            Toaster.shortToast("Please enter mobile no.");
            return;
        } else if (edtMobileNo.getText().toString().trim().length() < 10) {
            Toaster.shortToast("Please enter valid mobile no.");
            return;
        } else if (edtAddress.getText().toString().trim().isEmpty()) {
            Toaster.shortToast("Please enter address.");
            return;
        }
        doRegisterUser();
    }

    @SuppressLint("HardwareIds")
    public void doRegisterUser() {
        String version = "1.0.7";
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        JSONObject postData = HttpRequestHandler.getInstance().getRegisterUserParam(Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID),
                edtName.getText().toString(), edtPassword.getText().toString(), edtEmail.getText().toString(), edtMobileNo.getText().toString(),
                edtAddress.getText().toString(), isSeller, fb_id, google_id, version);

        if (postData != null) {
            new PostRequest(this, isSeller ? getString(R.string.registerSeller) : getString(R.string.registerUser), postData, true,
                    new PostRequest.OnPostServiceCallListener() {
                        @Override
                        public void onSucceedToPostCall(JSONObject response) {
                            UserDetailModel userDetailModel = new Gson().fromJson(response.toString(), UserDetailModel.class);
                            if (userDetailModel.status == 0) {
                                if (isSeller) {
                                    userDetailModel.loginSellerDetail.password = edtPassword.getText().toString();
                                    globals.setUserId(userDetailModel.loginSellerDetail.seller_reg_Id);
                                } else {
                                    userDetailModel.loginUserDetail.password = edtPassword.getText().toString();
                                    globals.setUserId(userDetailModel.loginUserDetail.user_reg_Id);
                                }
                                globals.setIsSeller(isSeller);
                                globals.setUserDetails(userDetailModel);
                                startActivity(new Intent(SignUpActivity.this, isSeller ? SellerDashboardActivity.class : DashboardActivity.class));
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

    @OnClick(R.id.ll_sign_up)
    public void onViewClicked() {
        Globals.hideKeyboard(this);
    }

}
