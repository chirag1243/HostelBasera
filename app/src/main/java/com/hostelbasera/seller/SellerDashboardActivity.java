package com.hostelbasera.seller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.hostelbasera.R;
import com.hostelbasera.apis.HttpRequestHandler;
import com.hostelbasera.apis.PostRequest;
import com.hostelbasera.main.DashboardActivity;
import com.hostelbasera.main.FragmentBookmarkList;
import com.hostelbasera.main.FragmentHome;
import com.hostelbasera.main.FragmentOrderList;
import com.hostelbasera.main.LoginActivity;
import com.hostelbasera.model.PropertyDetailModel;
import com.hostelbasera.utility.BaseActivity;
import com.hostelbasera.utility.Globals;
import com.hostelbasera.utility.Toaster;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import droidninja.filepicker.FilePickerConst;

public class SellerDashboardActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int MAP_BUTTON_REQUEST_CODE = 1232;
    private static final int Dashboard_REQUEST_CODE = 1243;
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
            case R.id.nav_enquiry:
                setToolbarTitle(R.string.enquiry);
                setFragment(new FragmentOrderList());
                //TODO : Do Comment it

//                Intent intent = new Intent(getApplicationContext(), PayMentGateWay.class);
//                intent.putExtra("FIRST_NAME", "Chirag Gandhi");
//                intent.putExtra("PHONE_NUMBER", "9843467834");
//                intent.putExtra("EMAIL_ADDRESS", "chirag3424@gmail.com   ");
//                intent.putExtra("RECHARGE_AMT", "1");
//                startActivity(intent);
                break;
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

    public void doLogout() {
        globals.setUserDetails(null);
        globals.setUserId(0);
        globals.clearAllSharedPreferences();
        startActivity(new Intent(SellerDashboardActivity.this, LoginActivity.class));
        finish();
    }

    @OnClick(R.id.fb_add_hostel)
    public void doAddHostel() {
        startActivityForResult(new Intent(SellerDashboardActivity.this, AddHostelPGActivity.class), Dashboard_REQUEST_CODE);// TODO : Change it  PricingActivity
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Dashboard_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                setFragment(new FragmentSellerHome());
            }
        }
    }
}
