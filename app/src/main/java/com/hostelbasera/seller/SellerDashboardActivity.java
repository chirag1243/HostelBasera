package com.hostelbasera.seller;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.hostelbasera.R;
import com.hostelbasera.apis.HttpRequestHandler;
import com.hostelbasera.apis.PostRequest;
import com.hostelbasera.main.DashboardActivity;
import com.hostelbasera.main.FragmentBookmarkList;
import com.hostelbasera.main.FragmentHome;
import com.hostelbasera.main.FragmentOrderList;
import com.hostelbasera.main.LoginActivity;
import com.hostelbasera.model.CheckSumModel;
import com.hostelbasera.model.PropertyDetailModel;
import com.hostelbasera.model.UserDetailModel;
import com.hostelbasera.utility.BaseActivity;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.Globals;
import com.hostelbasera.utility.Toaster;
import com.orhanobut.logger.Logger;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import droidninja.filepicker.FilePickerConst;

public class SellerDashboardActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, PermissionListener {

    private static final int MAP_BUTTON_REQUEST_CODE = 1232;
    private static final int Dashboard_REQUEST_CODE = 1243;
    private static final int UpdateCode = 2212;

    boolean doubleBackToExitPressedOnce = false;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fragmentContainer)
    FrameLayout fragmentContainer;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.fb_add_hostel)
    FloatingActionButton fbAddHostel;
    Globals globals;
    AppCompatImageView img_menu;
    CircleImageView imgProfile;
    public TextView tvNavTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_dashboard);
        ButterKnife.bind(this);
        init();
    }

    public void init() {
        globals = ((Globals) getApplicationContext());
        fbAddHostel.setVisibility(View.VISIBLE);
        updateChecker();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Globals.hideKeyboard(SellerDashboardActivity.this);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Drawable icMenu = ContextCompat.getDrawable(this, R.drawable.menu);
        icMenu.setColorFilter(getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_ATOP);

        toolbar.setNavigationIcon(icMenu);

        toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        View headerView = navView.getHeaderView(0);
        tvNavTitle = headerView.findViewById(R.id.tv_nav_title);
        img_menu = headerView.findViewById(R.id.img_menu);
        imgProfile = headerView.findViewById(R.id.img_profile);

        Glide.with(this)
                .load(getString(R.string.image_url) /*+ globals.getUserDetails().loginUserDetail.name*/)//TODO : Add Image
                .apply(new RequestOptions()
                        .fitCenter()
                        .placeholder(R.mipmap.ic_launcher)
                        .dontAnimate()
                        .priority(Priority.HIGH))
                .into(imgProfile);

        img_menu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                doCloseDrawer();
            }
        });

        setToolbarTitle(R.string.my_pg_hostel);

        tvNavTitle.setText(globals.getIsSeller() ? globals.getUserDetails().loginSellerDetail.name : globals.getUserDetails().loginUserDetail.name);

        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        setFragment(new FragmentSellerHome());
    }

    public void updateChecker() {
        UserDetailModel.VersionDetail versionDetail = globals.getUserDetails().loginSellerDetail.versionDetail;
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
                                dialog.dismiss();
                            }
                        });
            }
            builder.show();
        }
    }


    public void setToolbarTitle(int title) {
        toolbarTitle.setText(title);
    }

    public void setFragment(Fragment fragment) {
        if (getSupportFragmentManager().findFragmentById(R.id.fragmentContainer) != null) {
            // Remove currently loaded fragment from container
            getSupportFragmentManager()
                    .beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.fragmentContainer)).commit();
        }
        // Replace with new fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentContainer, fragment);
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            // Close drawer if open
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                finish();
            }

            this.doubleBackToExitPressedOnce = true;
            Toaster.shortToast(getString(R.string.back_again));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    public void doCloseDrawer() {
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_pg_hostel:
                setToolbarTitle(R.string.my_pg_hostel);
                setFragment(new FragmentSellerHome());
                break;
            case R.id.nav_booked_hostel_pg:
                setToolbarTitle(R.string.booked_hostel_pg);
                setFragment(new FragmentBookedList());
                break;
            /*case R.id.nav_enquiry:
                setToolbarTitle(R.string.enquiry);
                setFragment(new FragmentOrderList());
                break;*/
            case R.id.nav_change_password:
                setToolbarTitle(R.string.change_password);
                onChangePasswordClicked();
                doCloseDrawer();
                return false;
            case R.id.nav_contact_us:
                new TedPermission(this)
                        .setPermissionListener(this)
                        .setRationaleMessage(R.string.call_message)
                        .setDeniedMessage(R.string.call_denied_message)
                        .setGotoSettingButtonText(R.string.ok)
                        .setPermissions(Manifest.permission.CALL_PHONE)
                        .check();
                doCloseDrawer();
                return false;
            case R.id.nav_share_app:
                ShareCompat.IntentBuilder.from(this)
                        .setType("text/plain")
                        .setChooserTitle("Share via")
                        .setText("Check out the App at: \n http://play.google.com/store/apps/details?id=" + this.getPackageName())
                        .startChooser();
                doCloseDrawer();
                return false;
            case R.id.nav_sign_out:
                doLogout();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        doCloseDrawer();
        return true;
    }

    @Override
    public void onPermissionGranted() {
        String contact_no = "+917622885409";//Chintan

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + contact_no));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(callIntent);
    }

    @Override
    public void onPermissionDenied(ArrayList<String> deniedPermissions) {

    }

   /* public void setAddress(){
        LocationPickerIntent locationPickerIntent = LocationPickerActivity.Builder()
                .withLocation(41.4036299, 2.1743558)
                .withGeolocApiKey("<PUT API KEY HERE>")
                .withSearchZone("es_ES")
                .shouldReturnOkOnBackPressed()
                .withStreetHidden()
                .withCityHidden()
                .withZipCodeHidden()
                .withSatelliteViewHidden()
                .withGooglePlacesEnabled()
                .withGoogleTimeZoneEnabled()
                .withVoiceSearchHidden()
                .build(this);

        startActivityForResult(locationPickerIntent, MAP_BUTTON_REQUEST_CODE);
    }*/

    public void onChangePasswordClicked() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.MyEnquiryAlertDialogStyle);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.change_password_dialog, null);
        dialogBuilder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tv_title);
        EditText edtOldPassword = dialogView.findViewById(R.id.edt_old_password);
        EditText edtNewPassword = dialogView.findViewById(R.id.edt_new_password);
        EditText edtConfirmPassword = dialogView.findViewById(R.id.edt_confirm_password);

        TextView tvSubmit = dialogView.findViewById(R.id.tv_submit);
        TextView tvCancel = dialogView.findViewById(R.id.tv_cancel);


        AlertDialog alertDialog = dialogBuilder.create();

        tvTitle.setTypeface(tvTitle.getTypeface(), Typeface.BOLD);
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtOldPassword.getText().toString().trim().isEmpty()) {
                    Toaster.shortToast("Please enter old password");
                    return;
                }
                if (!edtOldPassword.getText().toString().trim().equals(globals.getUserDetails().loginSellerDetail.password)) {
                    Toaster.shortToast("Please enter valid old password");
                    return;
                }
                if (edtNewPassword.getText().toString().trim().isEmpty()) {
                    Toaster.shortToast("Please enter password");
                    return;
                }
                if (edtNewPassword.getText().toString().length() < 6) {
                    Toaster.shortToast("Password must be min 6 character.");
                    return;
                }

                if (edtConfirmPassword.getText().toString().trim().isEmpty()) {
                    Toaster.shortToast("Please enter confirm password");
                    return;
                }

                if (!edtNewPassword.getText().toString().trim().equals(edtConfirmPassword.getText().toString().trim())) {
                    Toaster.shortToast("Password & confirm password doesn't match");
                    return;
                }

                if (Globals.isNetworkAvailable(SellerDashboardActivity.this)) {
                    doChangePassword(edtNewPassword.getText().toString());
                    alertDialog.dismiss();
                } else {
                    Toaster.shortToast(getString(R.string.no_internet_msg));
                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void doChangePassword(String password) {
        JSONObject postData = HttpRequestHandler.getInstance().getChangePasswordParam(true, password);
        if (postData != null) {

            new PostRequest(this, getString(R.string.changePassword), postData, true, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    PropertyDetailModel propertyDetailModel = new Gson().fromJson(response.toString(), PropertyDetailModel.class);
                    UserDetailModel userDetailModel = new UserDetailModel();
                    userDetailModel = globals.getUserDetails();
                    userDetailModel.loginSellerDetail.password = password;
                    globals.setUserDetails(userDetailModel);

                    Toaster.shortToast(propertyDetailModel.message);
                }

                @Override
                public void onFailedToPostCall(int statusCode, String msg) {
                    Toaster.shortToast(msg);
                }
            }).execute();
        }
        Globals.hideKeyboard(this);
    }

    public void doLogout() {
        globals.setUserDetails(null);
        globals.setUserId(0);
        globals.clearAllSharedPreferences();
        startActivity(new Intent(SellerDashboardActivity.this, LoginActivity.class));
        finish();
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        setToolbarTitle(R.string.my_pg_hostel);
        setFragment(new FragmentSellerHome());
    }*/

    @OnClick(R.id.fb_add_hostel)
    public void doAddHostel() {
        OrderId = "Order" + Globals.randomNumber();
        CustId = "Cust" + Globals.randomNumber();
        Amount = "100.00";
        doGenerateChecksum();

//        startActivityForResult(new Intent(SellerDashboardActivity.this, AddHostelPGActivity.class), Dashboard_REQUEST_CODE);
    }

    String OrderId = "", CustId = "", Amount = "";

    private void doGenerateChecksum() {
        JSONObject postData = HttpRequestHandler.getInstance().doGenerateChecksumParam(OrderId, CustId, Amount);
        if (postData != null) {

            new PostRequest(this, getString(R.string.main_url), getString(R.string.generateChecksum), postData, true, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    CheckSumModel checkSumModel = new Gson().fromJson(response.toString(), CheckSumModel.class);
                    onStartTransaction(checkSumModel.CHECKSUMHASH);
                }

                @Override
                public void onFailedToPostCall(int statusCode, String msg) {
                    Toaster.shortToast(msg);
                }
            }).execute();
        }
        Globals.hideKeyboard(this);
    }


    public void onStartTransaction(String CHECKSUMHASH) {
        PaytmPGService Service = PaytmPGService.getProductionService();

        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put(Constant.MID, Constant.MID_Value);
// Key in your staging and production MID available in your dashboard
        paramMap.put(Constant.ORDER_ID, OrderId);
        paramMap.put(Constant.CUST_ID, CustId);
//        paramMap.put(Constant.MOBILE_NO, "9978261182");
//        paramMap.put(Constant.EMAIL, "chiragfriend.143@gmail.com");
        paramMap.put(Constant.INDUSTRY_TYPE_ID, Constant.INDUSTRY_TYPE_ID_Value);
        paramMap.put(Constant.CHANNEL_ID, Constant.CHANNEL_ID_Value);
        paramMap.put(Constant.TXN_AMOUNT, Amount);
        paramMap.put(Constant.WEBSITE, Constant.WEBSITE_Value);
// This is the staging value. Production value is available in your dashboard

// This is the staging value. Production value is available in your dashboard
        paramMap.put(Constant.CALLBACK_URL, Constant.CALLBACK_URL_Value+ OrderId);
        //"https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=order1");
        paramMap.put(Constant.CHECKSUMHASH, CHECKSUMHASH);

        PaytmOrder Order = new PaytmOrder(paramMap);

		/*PaytmMerchant Merchant = new PaytmMerchant(
				"https://pguat.paytm.com/paytmchecksum/paytmCheckSumGenerator.jsp",
				"https://pguat.paytm.com/paytmchecksum/paytmCheckSumVerify.jsp");*/

        Service.initialize(Order, null);

        Service.startPaymentTransaction(this, true, true,
                new PaytmPaymentTransactionCallback() {
                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        // Some UI Error Occurred in Payment Gateway Activity.
                        // // This may be due to initialization of views in
                        // Payment Gateway Activity or may be due to //
                        // initialization of webview. // Error Message details
                        // the error occurred.
                    }


                    @Override
                    public void onTransactionResponse(Bundle inResponse) {
                        Logger.e("LOG", "Payment Transaction is successful " + inResponse);
                        Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void networkNotAvailable() { // If network is not
                        // available, then this
                        // method gets called.
                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        // This method gets called if client authentication
                        // failed. // Failure may be due to following reasons //
                        // 1. Server error or downtime. // 2. Server unable to
                        // generate checksum or checksum response is not in
                        // proper format. // 3. Server failed to authenticate
                        // that client. That is value of payt_STATUS is 2. //
                        // Error Message describes the reason for failure.
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {

                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        Toast.makeText(SellerDashboardActivity.this, "Back pressed. Transaction cancelled", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                        Logger.d("LOG", "Payment Transaction Failed " + inErrorMessage);
                        Toast.makeText(getBaseContext(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
                    }

                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Dashboard_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
            setFragment(new FragmentSellerHome());
//            }
        }
        if (requestCode == UpdateCode) {
            updateChecker();
        }

    }
}
