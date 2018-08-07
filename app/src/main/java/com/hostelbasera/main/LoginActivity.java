package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hostelbasera.R;
import com.hostelbasera.apis.HttpRequestHandler;
import com.hostelbasera.apis.PostRequest;
import com.hostelbasera.model.UserDetailModel;
import com.hostelbasera.utility.BaseActivity;
import com.hostelbasera.utility.Globals;
import com.hostelbasera.utility.Toaster;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.hoang8f.android.segmented.SegmentedGroup;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.rb_buyer)
    RadioButton rbBuyer;
    @BindView(R.id.rb_seller)
    RadioButton rbSeller;
    @BindView(R.id.segmented_group)
    SegmentedGroup segmentedGroup;
    @BindView(R.id.edt_email)
    EditText edtEmail;
    @BindView(R.id.edt_password)
    EditText edtPassword;
    @BindView(R.id.btn_sign_in)
    Button btnSignIn;
    @BindView(R.id.tv_forget_password)
    TextView tvForgetPassword;
    @BindView(R.id.tv_don_t_have_an_account_)
    TextView tvDonTHaveAnAccount;
    @BindView(R.id.sign_up)
    TextView signUp;
    @BindView(R.id.ll_sign_up)
    LinearLayout llSignUp;

    boolean isSeller;
    Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        globals = ((Globals) getApplicationContext());
        init();
    }

    public void init() {
        btnSignIn.setTypeface(btnSignIn.getTypeface(), Typeface.BOLD);
        signUp.setTypeface(signUp.getTypeface(), Typeface.BOLD);
        segmentedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_buyer) {
                    Toaster.shortToast("Buyer");
                    isSeller = false;
                } else {
                    Toaster.shortToast("Seller");
                    isSeller = true;
                }
            }
        });
    }

    @OnClick(R.id.btn_sign_in)
    public void onBtnSignInClicked() {
        if (edtEmail.getText().toString().trim().isEmpty()) {
            Toaster.shortToast("Please enter email id.");
            return;
        } else if (edtPassword.getText().toString().trim().isEmpty()) {
            Toaster.shortToast("Please enter password.");
            return;
        } else if (edtPassword.getText().toString().length() < 6) {
            Toaster.shortToast("Password must be min 6 character.");
            return;
        }
        doLogin();
    }

    @SuppressLint("HardwareIds")
    public void doLogin() {
        JSONObject postData = HttpRequestHandler.getInstance().getLoginUserParam(Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID), edtEmail.getText().toString(), edtPassword.getText().toString(), isSeller);

        if (postData != null) {
            new PostRequest(this, isSeller ? getString(R.string.loginSeller) : getString(R.string.loginUser), postData, true,
                    new PostRequest.OnPostServiceCallListener() {
                        @Override
                        public void onSucceedToPostCall(JSONObject response) {
                            UserDetailModel userDetailModel = new Gson().fromJson(response.toString(), UserDetailModel.class);
                            if (userDetailModel.status == 0) {
                                if (isSeller)
                                    userDetailModel.loginSellerDetail.password = edtPassword.getText().toString();
                                else
                                    userDetailModel.loginUserDetail.password = edtPassword.getText().toString();
                                globals.setIsSeller(isSeller);
                                globals.setUserDetails(userDetailModel);
                                startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
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

    @OnClick(R.id.tv_forget_password)
    public void onTvForgetPasswordClicked() {
        Toaster.shortToast("forget password");
    }

    @OnClick(R.id.ll_sign_up)
    public void onLlSignUpClicked() {
        startActivity(new Intent(this,SignUpActivity.class));
    }
}
