package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hostelbasera.R;
import com.hostelbasera.apis.HttpRequestHandler;
import com.hostelbasera.apis.PostRequest;
import com.hostelbasera.model.CheckMobilenoForOtpModel;
import com.hostelbasera.model.GetAllDropdownListModel;
import com.hostelbasera.model.UserDetailModel;
import com.hostelbasera.utility.BaseActivity;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.Globals;
import com.hostelbasera.utility.Toaster;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.hoang8f.android.segmented.SegmentedGroup;
import io.ghyeok.stickyswitch.widget.StickySwitch;

import static com.hostelbasera.utility.Constant.Verify_ID_Login;

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
    //    @BindView(R.id.edt_password)
//    AppCompatEditText edtPassword;
    @BindView(R.id.edt_mobile_no)
    AppCompatEditText edtMobileNo;
    //    @BindView(R.id.edt_address)
//    AppCompatEditText edtAddress;
    @BindView(R.id.btn_sign_up)
    Button btnSignUp;

    @BindView(R.id.sp_city)
    Spinner spCity;

    @BindView(R.id.sticky_switch)
    StickySwitch stickySwitch;


    ArrayList<GetAllDropdownListModel.City_list> arrCityDetail;

    boolean isSeller;
    Globals globals;
    @BindView(R.id.ll_sign_up)
    LinearLayout llSignUp;
    Intent intent;
    String fb_id = "", google_id = "", gender = "male";
    int city_id = 0;
    @BindView(R.id.ll_city)
    LinearLayout llCity;
    @BindView(R.id.tv_already_have_an_account)
    TextView tvAlreadyHaveAnAccount;
    @BindView(R.id.tv_sign_in)
    TextView tvSignIn;
    @BindView(R.id.ll_back_sign)
    LinearLayout llBackSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        if (Globals.isNetworkAvailable(SignUpActivity.this)) {
            getAllDropdownList();
        } else {
            Toaster.shortToast(getString(R.string.no_internet_msg));
            finish();
        }
        init();
    }

    public void getAllDropdownList() {
        JSONObject postData = HttpRequestHandler.getInstance().getAllDropdownListParam();

        if (postData != null) {
            new PostRequest(SignUpActivity.this, getString(R.string.getAllDropdownList), postData, true, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    GetAllDropdownListModel getAllDropdownListModel = new Gson().fromJson(response.toString(), GetAllDropdownListModel.class);
                    if (getAllDropdownListModel.status == 0 && getAllDropdownListModel.allDropdownListDetail != null && getAllDropdownListModel.allDropdownListDetail.city_list != null && !getAllDropdownListModel.allDropdownListDetail.city_list.isEmpty()) {
                        arrCityDetail = getAllDropdownListModel.allDropdownListDetail.city_list;
                        setSpCity();
                    } else {
                        Toaster.shortToast(getAllDropdownListModel.message);
                    }
                }

                @Override
                public void onFailedToPostCall(int statusCode, String msg) {
                    Toaster.shortToast(msg);
                }
            }).execute();
        }
        Globals.hideKeyboard(this);
    }


    public void setSpCity() {
//        arrCityDetail = new ArrayList<>();

        GetAllDropdownListModel.City_list cityListModel = new GetAllDropdownListModel.City_list();
        cityListModel.city_id = 0;
        cityListModel.city_name = "Select City";

        arrCityDetail.add(0, cityListModel);

        ArrayAdapter<GetAllDropdownListModel.City_list> adapterCityList = new ArrayAdapter<GetAllDropdownListModel.City_list>(getApplicationContext(), R.layout.spinner_city_item, arrCityDetail) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            // Change color item
            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View mView = super.getDropDownView(position, convertView, parent);
                TextView mTextView = (TextView) mView;
                if (position == 0) {
                    mTextView.setTextColor(Color.GRAY);
                } else {
                    mTextView.setTextColor(Color.BLACK);
                }
                return mView;
            }
        };

        spCity.setAdapter(adapterCityList);

        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                city_id = arrCityDetail.get(position).city_id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

        stickySwitch.setOnSelectedChangeListener(new StickySwitch.OnSelectedChangeListener() {
            @Override
            public void onSelectedChange(@NotNull StickySwitch.Direction direction, @NotNull String text) {
//                Toaster.shortToast("Now Selected : " + direction.name() + ", Current Text : " + text);
                gender = text.toLowerCase();
            }
        });

//        Globals.doBoldTextView(tvAlreadyHaveAnAccount);
        Globals.doBoldTextView(tvSignIn);
    }

    @OnClick(R.id.btn_sign_up)
    public void onSignUpClicked() {
        if (edtName.getText().toString().trim().isEmpty()) {
            Toaster.shortToast("Please enter name.");
            return;
        } /*else if (edtEmail.getText().toString().trim().isEmpty()) {
            Toaster.shortToast("Please enter email id.");
            return;
        }*/ else if (city_id == 0) {
            Toaster.shortToast("Please select city.");
            return;
        }/*else if (edtPassword.getText().toString().trim().isEmpty()) {
            Toaster.shortToast("Please enter password.");
            return;
        } else if (edtPassword.getText().toString().length() < 6) {
            Toaster.shortToast("Password must be min 6 character.");
            return;
        }  else if (edtAddress.getText().toString().trim().isEmpty()) {
            Toaster.shortToast("Please enter address.");
            return;
        } */ else if (edtMobileNo.getText().toString().trim().isEmpty()) {
            Toaster.shortToast("Please enter mobile no.");
            return;
        } else if (edtMobileNo.getText().toString().trim().length() < 10) {
            Toaster.shortToast("Please enter valid mobile no.");
            return;
        }
        doRegisterUser();
    }

    @SuppressLint("HardwareIds")
    public void doRegisterUser() {
        String version = "1.2.0";
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        JSONObject postData = HttpRequestHandler.getInstance().getRegisterUserParam(Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID),
                edtName.getText().toString(), "", edtEmail.getText().toString(), edtMobileNo.getText().toString(),
                "", isSeller, fb_id, google_id, version, gender, city_id);

        if (postData != null) {
            new PostRequest(this, isSeller ? getString(R.string.registerSeller) : getString(R.string.registerUser), postData, true,
                    new PostRequest.OnPostServiceCallListener() {
                        @Override
                        public void onSucceedToPostCall(JSONObject response) {
                            UserDetailModel userDetailModel = new Gson().fromJson(response.toString(), UserDetailModel.class);
                            if (userDetailModel.status == 0) {
                                /*if (isSeller) {
                                    userDetailModel.loginSellerDetail.password = edtPassword.getText().toString();
                                    globals.setNewUserId(userDetailModel.loginSellerDetail.seller_reg_Id);
                                } else {
                                    userDetailModel.loginUserDetail.password = edtPassword.getText().toString();
                                    globals.setNewUserId(userDetailModel.loginUserDetail.user_reg_Id);
                                }
                                globals.setIsSeller(isSeller);
                                globals.setUserDetails(userDetailModel);
                                startActivity(new Intent(SignUpActivity.this, isSeller ? SellerDashboardActivity.class : DashboardActivity.class));
                                finish();*/

                                if (Globals.isNetworkAvailable(SignUpActivity.this)) {
                                    doCheckMobilenoForOtp(edtMobileNo.getText().toString(), Verify_ID_Login);
                                } else {
                                    Toaster.shortToast(getString(R.string.no_internet_msg));
                                }
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


    public void doCheckMobilenoForOtp(String mobile_no, int verify_type) {
        JSONObject postData = HttpRequestHandler.getInstance().getCheckMobilenoForOtpParam(mobile_no, isSeller, verify_type);

        if (postData != null) {
            new PostRequest(this, getString(R.string.checkMobilenoForOtp), postData, true,
                    new PostRequest.OnPostServiceCallListener() {
                        @Override
                        public void onSucceedToPostCall(JSONObject response) {
                            CheckMobilenoForOtpModel mobilenoForOtpModel = new Gson().fromJson(response.toString(), CheckMobilenoForOtpModel.class);
                            /*if (mobilenoForOtpModel.status == 0) {
                            } */
                            Toaster.shortToast(mobilenoForOtpModel.message);
                            startActivity(new Intent(SignUpActivity.this, OTPVerifyActivity.class)
                                    .putExtra(Constant.IsSeller, isSeller)
                                    .putExtra(Constant.Mobile_no, mobile_no)
                            );

                        }

                        @Override
                        public void onFailedToPostCall(int statusCode, String msg) {
                            Toaster.shortToast(msg);
                        }
                    }).execute();
        }
    }


    @OnClick(R.id.ll_sign_up)
    public void onViewClicked() {
        Globals.hideKeyboard(this);
    }

    @OnClick(R.id.ll_back_sign)
    public void onBackSignClicked() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
