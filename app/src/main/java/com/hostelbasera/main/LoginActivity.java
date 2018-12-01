package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.AppCompatEditText;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
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
import com.hostelbasera.utility.Toaster;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.hoang8f.android.segmented.SegmentedGroup;

public class LoginActivity extends BaseActivity {

    private static final String TAG = "Login Activity";
    @BindView(R.id.rb_buyer)
    RadioButton rbBuyer;
    @BindView(R.id.rb_seller)
    RadioButton rbSeller;
    @BindView(R.id.segmented_group)
    SegmentedGroup segmentedGroup;
    @BindView(R.id.edt_email)
    AppCompatEditText edtEmail;
    @BindView(R.id.edt_password)
    AppCompatEditText edtPassword;
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

    @BindView(R.id.sign_in_button)
    ImageView signInButton;
    boolean isSeller;
    Globals globals;
    CallbackManager callbackManager;
    GoogleSignInClient mGoogleSignInClient;

    private static final int RC_SIGN_IN = 12121;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        globals = ((Globals) getApplicationContext());

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        init();
    }

    public void init() {
        btnSignIn.setTypeface(btnSignIn.getTypeface(), Typeface.BOLD);
        signUp.setTypeface(signUp.getTypeface(), Typeface.BOLD);

//        signInButton.setSize(SignInButton.SIZE_STANDARD);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        try {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            Toaster.shortToast(account.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
        }


        segmentedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                isSeller = checkedId != R.id.rb_buyer;
            }
        });
    }

    @OnClick(R.id.login_button)
    public void facebookLogin() {
        if (isSeller) {
            Toaster.shortToast("Coming Soon");
            return;
        }
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        // App code
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.v("LoginActivity", response.toString());

                                        // Application code
                                        try {
                                            doCheckExitingUser(object.getString("email"), object.getString("name"),object.getString("id"),"");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender,birthday");
                        request.setParameters(parameters);
                        request.executeAsync();
//                        Toaster.shortToast("On Success" + loginResult.toString());
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }

    public void doCheckExitingUser(String email, String name, String fb_id, String google_id) {
        JSONObject postData = HttpRequestHandler.getInstance().getCheckExitingUserParam(email);

        if (postData != null) {
            new PostRequest(this, getString(R.string.checkExitingUser), postData, true,
                    new PostRequest.OnPostServiceCallListener() {
                        @Override
                        public void onSucceedToPostCall(JSONObject response) {
                            UserDetailModel userDetailModel = new Gson().fromJson(response.toString(), UserDetailModel.class);
                            if (userDetailModel.status == 0 && userDetailModel.is_exiting == 1) {
                                if (isSeller) {
                                    userDetailModel.loginSellerDetail.password = edtPassword.getText().toString();
                                    globals.setUserId(userDetailModel.loginSellerDetail.seller_reg_Id);
                                } else {
                                    userDetailModel.loginUserDetail.password = edtPassword.getText().toString();
                                    globals.setUserId(userDetailModel.loginUserDetail.user_reg_Id);
                                }

                                globals.setIsSeller(isSeller);
                                globals.setUserDetails(userDetailModel);
                                startActivity(new Intent(LoginActivity.this, isSeller ? SellerDashboardActivity.class : DashboardActivity.class));
                                finish();
                            } else {
                                startActivity(new Intent(LoginActivity.this, SignUpActivity.class)
                                        .putExtra(Constant.Email, email)
                                        .putExtra(Constant.Full_name, name)
                                        .putExtra(Constant.Fb_id, fb_id)
                                        .putExtra(Constant.Google_id, google_id)
                                );
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

    @OnClick(R.id.sign_in_button)
    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            Toaster.shortToast(account.getEmail());
            // Signed in successfully, show authenticated UI.
//            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toaster.shortToast("Failed Login");
//            updateUI(null);
        }
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

    /*@OnClick(R.id.login_button)
    public void connectFacebook() {
        List<String> scopes = Arrays.asList("user_birthday", "user_friends");

        SimpleAuth.connectFacebook(scopes, new AuthCallback() {
            @Override
            public void onSuccess(SocialUser socialUser) {
                Log.d(TAG, "userId:" + socialUser.userId);
                Log.d(TAG, "email:" + socialUser.email);
                Log.d(TAG, "accessToken:" + socialUser.accessToken);
                Log.d(TAG, "profilePictureUrl:" + socialUser.profilePictureUrl);
                Log.d(TAG, "username:" + socialUser.username);
                Log.d(TAG, "fullName:" + socialUser.fullName);
                Log.d(TAG, "pageLink:" + socialUser.pageLink);
            }

            @Override
            public void onError(Throwable error) {
                Log.d(TAG, error.getMessage());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Canceled");
            }
        });

        *//*com.jaychang.sa.google.SimpleAuth.connectGoogle(scopes, new AuthCallback() {
            @Override
            public void onSuccess(SocialUser socialUser) {
//                socialUser.
            }

            @Override
            public void onError(Throwable error) {

            }

            @Override
            public void onCancel() {

            }
        });*//*
    }*/

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
                                if (isSeller) {
                                    userDetailModel.loginSellerDetail.password = edtPassword.getText().toString();
                                    globals.setUserId(userDetailModel.loginSellerDetail.seller_reg_Id);
                                } else {
                                    userDetailModel.loginUserDetail.password = edtPassword.getText().toString();
                                    globals.setUserId(userDetailModel.loginUserDetail.user_reg_Id);
                                }
                                globals.setIsSeller(isSeller);
                                globals.setUserDetails(userDetailModel);
                                startActivity(new Intent(LoginActivity.this, isSeller ? SellerDashboardActivity.class : DashboardActivity.class));
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
        startActivity(new Intent(this, SignUpActivity.class));
    }
}
