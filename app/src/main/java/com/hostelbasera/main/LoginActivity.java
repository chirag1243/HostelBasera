package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    GoogleApiClient mGoogleApiClient;

    private static final int RC_SIGN_IN = 12121;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        globals = ((Globals) getApplicationContext());


       /* try {
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

        }*/
        init();
    }

    public void init() {
        btnSignIn.setTypeface(btnSignIn.getTypeface(), Typeface.BOLD);
        signUp.setTypeface(signUp.getTypeface(), Typeface.BOLD);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestId()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();

        try {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//            Toaster.shortToast(account.getEmail());
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

        try {
            LoginManager.getInstance().logOut();
        } catch (Exception e) {
            e.printStackTrace();
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
                                            doCheckExitingUser(object.getString("email"), object.getString("name"), object.getString("id"), "");
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
        JSONObject postData = HttpRequestHandler.getInstance().getCheckExitingUserParam(email,isSeller);

        if (postData != null) {
            new PostRequest(this, isSeller ? getString(R.string.checkExitingSeller) : getString(R.string.checkExitingUser), postData, true,
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
                                        .putExtra(Constant.IsSeller,isSeller)
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
        if (isSeller) {
            Toaster.shortToast("Coming Soon");
            return;
        }

        try {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
//                        Toaster.shortToast("Sign Out Google : ");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

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
            try {
                doCheckExitingUser(account.getEmail(), account.getGivenName(), "", account.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Signed in successfully, show authenticated UI.
//            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toaster.shortToast("Google Login Failed");
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

    @SuppressLint("HardwareIds")
    public void doLogin() {
        String version = "1.0.1";
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        JSONObject postData = HttpRequestHandler.getInstance().getLoginUserParam(Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID), edtEmail.getText().toString(), edtPassword.getText().toString(), isSeller, version);

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
