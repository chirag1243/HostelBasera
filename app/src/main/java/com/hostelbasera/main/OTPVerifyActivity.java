package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.hostelbasera.utility.OtpReceivedInterface;
import com.hostelbasera.utility.SmsBroadcastReceiver;
import com.hostelbasera.utility.Toaster;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OTPVerifyActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        OtpReceivedInterface, GoogleApiClient.OnConnectionFailedListener{

    @BindView(R.id.tv_otp)
    TextView tvOtp;
    @BindView(R.id.edt_otp)
    OtpEditText edtOtp;
    @BindView(R.id.btn_verify)
    Button btnVerify;

    String mobile_no = "";
    boolean isSeller;
    Globals globals;

    GoogleApiClient mGoogleApiClient;

    SmsBroadcastReceiver mSmsBroadcastReceiver;
    private int RESOLVE_HINT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverify);
        ButterKnife.bind(this);
        globals = ((Globals) getApplicationContext());

        mSmsBroadcastReceiver = new SmsBroadcastReceiver();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();

        mSmsBroadcastReceiver.setOnOtpListeners(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        getApplicationContext().registerReceiver(mSmsBroadcastReceiver, intentFilter);

        startSMSListener();

        mobile_no = getIntent().getStringExtra(Constant.Mobile_no);
        isSeller = getIntent().getBooleanExtra(Constant.IsSeller, false);

        edtOtp.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 6) {
                    if (Globals.isNetworkAvailable(OTPVerifyActivity.this)) {
                        onViewClicked();
                    } else {
                        Toaster.shortToast(getString(R.string.no_internet_msg));
                    }
                }
            }
        });
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

    String version = "1.1.8";

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

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onOtpReceived(String otp) {
        Toaster.shortToast("Otp Received");
        edtOtp.setText(otp);
    }

    @Override
    public void onOtpTimeout() {
        Toaster.shortToast("Time out, please resend");
    }

    public void startSMSListener() {
        SmsRetrieverClient mClient = SmsRetriever.getClient(this);
        Task<Void> mTask = mClient.startSmsRetriever();
        mTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                Toaster.shortToast("SMS Retriever starts");
//                IntentFilter filter = new IntentFilter();
//                filter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
//                registerReceiver(new SmsBroadcastReceiver(), filter);
            }
        });
        mTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toaster.shortToast("Error");
            }
        });
    }
}
