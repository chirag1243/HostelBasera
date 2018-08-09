package com.hostelbasera.main;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.hostelbasera.R;
import com.hostelbasera.utility.BaseActivity;
import com.hostelbasera.utility.Globals;
import com.hostelbasera.utility.Toaster;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

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
                .load(getString(R.string.image_url) + globals.getUserDetails().loginUserDetail.name)//TODO : Add Image
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

        setToolbarTitle(R.string.find_your_hostel);

        tvNavTitle.setText(globals.getIsSeller() ? globals.getUserDetails().loginSellerDetail.name : globals.getUserDetails().loginUserDetail.name);

        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        setFragment(new FragmentHome());
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
                setToolbarTitle(R.string.pg_hostel);
                setFragment(new FragmentHome());
                break;
            case R.id.nav_bookmarks:
                setToolbarTitle(R.string.bookmarks);
//                setFragment(new FragmentClientList());
                break;
            case R.id.nav_my_pg_hostel:
                setToolbarTitle(R.string.my_pg_hostel);
//                setFragment(new FragmentTransportList());
                break;
            case R.id.nav_feedback:
                setToolbarTitle(R.string.feedback);
//                setFragment(new FragmentOrderList());
                break;
            case R.id.nav_share_app:
                setToolbarTitle(R.string.share_app);
//                setFragment(new FragmentStockList());
                break;
            case R.id.nav_sign_out:
                doLogout();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        doCloseDrawer();
        return true;
    }

    public void doLogout() {
        globals.setUserDetails(null);
        globals.setUserId(0);
        globals.clearAllSharedPreferences();
        startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
        finish();
    }
}
