package com.hostelbasera.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

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
import com.hostelbasera.model.PropertyDetailModel;
import com.hostelbasera.model.UserDetailModel;
import com.hostelbasera.seller.FragmentBookedList;
import com.hostelbasera.utility.BaseActivity;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.Globals;
import com.hostelbasera.utility.Toaster;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class DashboardActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, PermissionListener {

    private static final int UpdateCode = 2212, filterCode = 1005;

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
    Globals globals;
    AppCompatImageView img_menu;
    CircleImageView imgProfile;
    public TextView tvNavTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
        init();
    }

    public void init() {
        globals = ((Globals) getApplicationContext());
        updateChecker();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Globals.hideKeyboard(DashboardActivity.this);
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

        setToolbarTitle(R.string.all_cities);
        toolbarTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.down_arrow, 0);
        toolbarTitle.setCompoundDrawablePadding(5);
        globals.setCityId(0);
        toolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(DashboardActivity.this, CityActivity.class), Constant.CityCode);
            }
        });

        tvNavTitle.setText(globals.getIsSeller() ? globals.getUserDetails().loginSellerDetail.name : globals.getUserDetails().loginUserDetail.name);

        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        setFragment(new FragmentHome());

        if (getIntent().hasExtra(Constant.Property_id) && getIntent().getIntExtra(Constant.Property_id, 0) > 0) {
            startActivity(new Intent(DashboardActivity.this, HostelDetailActivity.class)
                    .putExtra(Constant.Property_id, getIntent().getIntExtra(Constant.Property_id, 0))
                    .putExtra(Constant.Property_name, getIntent().getStringExtra(Constant.Property_name))
            );
        }
    }

    public void updateChecker() {
        UserDetailModel.VersionDetail versionDetail = globals.getUserDetails().loginUserDetail.versionDetail;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UpdateCode) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    updateChecker();
                }
            }
        }

        if (requestCode == Constant.CityCode && data != null) {
            toolbarTitle.setText(data.getStringExtra(Constant.City_Name).equals(getString(R.string.all)) ? getString(R.string.all_cities) : data.getStringExtra(Constant.City_Name));
            setFragment(new FragmentHome());
        }

        if (requestCode == filterCode) {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    public void setToolbarTitle(int title) {
        toolbarTitle.setText(title);
        toolbarTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        toolbarTitle.setCompoundDrawablePadding(0);
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
                setToolbarTitle(R.string.all_cities);
                globals.setCityId(0);
                toolbarTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.down_arrow, 0);
                toolbarTitle.setCompoundDrawablePadding(5);
                setFragment(new FragmentHome());
                break;
            case R.id.nav_bookmarks:
                setToolbarTitle(R.string.bookmarks);
                setFragment(new FragmentBookmarkList());
                break;
            case R.id.nav_booking_request:
                setToolbarTitle(R.string.booking_request);
                setFragment(FragmentBookingList.newInstance(false));
                break;
            case R.id.nav_booked_hostel_pg:
                setToolbarTitle(R.string.booked_hostel_pg);
                setFragment(FragmentBookedList.newInstance(false));
                break;
            case R.id.nav_feedback:
                onFeedbackClicked();
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
//                setToolbarTitle(R.string.share_app);
//                Toaster.shortToast("Coming Soon");
                ShareCompat.IntentBuilder.from(this)
                        .setType("text/plain")
                        .setChooserTitle("Share via")
                        .setText("Check out the App at: \n http://play.google.com/store/apps/details?id=" + this.getPackageName())
                        .startChooser();
                doCloseDrawer();
                return false;
//                break;
            case R.id.nav_sign_out:
                doLogout();
                break;

            case R.id.nav_about_us:
                setToolbarTitle(R.string.about_us);
                new FragmentPolicyDetails();

                setFragment(FragmentPolicyDetails.newInstance(Constant.About_us));
                break;
            case R.id.nav_privacy_policy:
                setToolbarTitle(R.string.privacy_policy);
                setFragment(FragmentPolicyDetails.newInstance(Constant.Privacy_policy));
                break;
            case R.id.nav_refund_policy:
                setToolbarTitle(R.string.refund_policy);
                setFragment(FragmentPolicyDetails.newInstance(Constant.Refund_policy));
                break;
            case R.id.nav_booking_policy:
                setToolbarTitle(R.string.booking_policy);
                setFragment(FragmentPolicyDetails.newInstance(Constant.Booking_policy));
                break;
            case R.id.nav_non_discrimination_policy:
                setToolbarTitle(R.string.non_discrimination_policy);
                setFragment(FragmentPolicyDetails.newInstance(Constant.Non_discrimination_policy));
                break;
            case R.id.nav_guest_refund_policy:
                setToolbarTitle(R.string.guest_refund_policy);
                setFragment(FragmentPolicyDetails.newInstance(Constant.Guest_refund_policy));
                break;
            case R.id.nav_service_terms:
                setToolbarTitle(R.string.service_terms);
                setFragment(FragmentPolicyDetails.newInstance(Constant.Service_terms));
                break;
            case R.id.nav_terms_and_conditions:
                setToolbarTitle(R.string.terms_and_conditions);
                setFragment(FragmentPolicyDetails.newInstance(Constant.Terms_and_conditions));
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

    public void onFeedbackClicked() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.MyEnquiryAlertDialogStyle);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.feedback_dialog, null);
        dialogBuilder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tv_title);
        EditText edtTitle = dialogView.findViewById(R.id.edt_title);
        EditText edtDescription = dialogView.findViewById(R.id.edt_description);
        TextView tvSubmit = dialogView.findViewById(R.id.tv_submit);
        TextView tvCancel = dialogView.findViewById(R.id.tv_cancel);
        AlertDialog alertDialog = dialogBuilder.create();

        tvTitle.setTypeface(tvTitle.getTypeface(), Typeface.BOLD);
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtTitle.getText().toString().trim().isEmpty()) {
                    Toaster.shortToast("Please write your title here ...");
                    return;
                }
                if (edtDescription.getText().toString().trim().isEmpty()) {
                    Toaster.shortToast("Please write your description here ...");
                    return;
                }
                if (Globals.isNetworkAvailable(DashboardActivity.this)) {
                    doGiveFeedback(edtTitle.getText().toString(), edtDescription.getText().toString());
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

    private void doGiveFeedback(String title, String description) {

        JSONObject postData = HttpRequestHandler.getInstance().getAddFeedbackDataParam(title, description);
        if (postData != null) {

            new PostRequest(this, getString(R.string.addFeedback), postData, true, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    PropertyDetailModel propertyDetailModel = new Gson().fromJson(response.toString(), PropertyDetailModel.class);
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
                if (!edtOldPassword.getText().toString().trim().equals(globals.getUserDetails().loginUserDetail.password)) {
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

                if (Globals.isNetworkAvailable(DashboardActivity.this)) {
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
        JSONObject postData = HttpRequestHandler.getInstance().getChangePasswordParam(false, password, globals.getUserDetails().loginUserDetail.user_reg_Id);
        if (postData != null) {

            new PostRequest(this, getString(R.string.changePassword), postData, true, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    PropertyDetailModel propertyDetailModel = new Gson().fromJson(response.toString(), PropertyDetailModel.class);
                    UserDetailModel userDetailModel = new UserDetailModel();
                    userDetailModel = globals.getUserDetails();
                    userDetailModel.loginUserDetail.password = password;
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
        globals.setNewUserId(0);
        globals.clearAllSharedPreferences();
        startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
        finish();
    }
}
