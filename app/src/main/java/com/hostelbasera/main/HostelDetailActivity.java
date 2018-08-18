package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hostelbasera.R;
import com.hostelbasera.apis.HttpRequestHandler;
import com.hostelbasera.apis.PostRequest;
import com.hostelbasera.model.PropertyDetailModel;
import com.hostelbasera.utility.BaseActivity;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.DetailImagePagerAdapter;
import com.hostelbasera.utility.Globals;
import com.hostelbasera.utility.Toaster;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HostelDetailActivity extends BaseActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.vp_image)
    ViewPager vpImage;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_type_of_property)
    TextView tvTypeOfProperty;
    @BindView(R.id.tv_size_of_property)
    TextView tvSizeOfProperty;
    @BindView(R.id.img_female)
    ImageView imgFemale;
    @BindView(R.id.img_male)
    ImageView imgMale;
    @BindView(R.id.img_male_female)
    ImageView imgMaleFemale;
    @BindView(R.id.rv_amenities)
    RecyclerView rvAmenities;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_bookmark)
    TextView tvBookmark;
    @BindView(R.id.tv_review)
    TextView tvReview;
    @BindView(R.id.tv_rate)
    TextView tvRate;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.img_share)
    ImageView imgShare;
    int property_id;

    PropertyDetailModel.PropertyDetails propertyDetails;
    Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.
    AdapterAmenities adapterAmenities;
    @BindView(R.id.btn_book_now)
    Button btnBookNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostel_detail);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        property_id = getIntent().getIntExtra(Constant.Property_id, 0);
        imgBack.setVisibility(View.VISIBLE);
        imgShare.setVisibility(View.VISIBLE);
        btnBookNow.setTypeface(btnBookNow.getTypeface(), Typeface.BOLD);
        if (Globals.isNetworkAvailable(this)) {
            getPropertyListData();
        } else {
            finish();
            Toaster.shortToast(R.string.no_internet_msg);
        }
    }

    public void getPropertyListData() {
        JSONObject postData = HttpRequestHandler.getInstance().getPropertyDetailsParam(property_id);

        if (postData != null) {

            new PostRequest(this, getString(R.string.getPropertyDetails), postData, true, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    PropertyDetailModel propertyDetailModel = new Gson().fromJson(response.toString(), PropertyDetailModel.class);
                    if (propertyDetailModel.status == 0 && propertyDetailModel.propertyDetails != null) {
                        propertyDetails = propertyDetailModel.propertyDetails;
                        setPropertyDetials();
                    } else {
                        Toaster.shortToast(propertyDetailModel.message);
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

    @SuppressLint("SetTextI18n")
    private void setPropertyDetials() {
        toolbarTitle.setText(propertyDetails.property_name);
        tvAddress.setText(propertyDetails.address);
        tvTypeOfProperty.setText(propertyDetails.property_type);
        tvSizeOfProperty.setText(propertyDetails.property_size);
        tvPrice.setText("₹ " + propertyDetails.price);
        setHostelFor();
        setImagePager();
        setAmenitiesAdapter();
    }

    private void setAmenitiesAdapter() {
        if (adapterAmenities == null) {
            adapterAmenities = new AdapterAmenities(this);
        }
        adapterAmenities.doRefresh(propertyDetails.propertyFacility);

        if (rvAmenities.getAdapter() == null) {
            rvAmenities.setHasFixedSize(false);
            rvAmenities.setLayoutManager(new GridLayoutManager(this, 4));
            rvAmenities.setItemAnimator(new DefaultItemAnimator());
            rvAmenities.setAdapter(adapterAmenities);
        }
    }

    public void setHostelFor() {
        if (propertyDetails.property_category_id == 1) {
            imgMale.setImageResource(R.drawable.male_select);
            imgFemale.setImageResource(R.drawable.female_de_select);
            imgMaleFemale.setImageResource(R.drawable.malefemale_de_select);
        } else if (propertyDetails.property_category_id == 2) {
            imgMale.setImageResource(R.drawable.male_de_select);
            imgFemale.setImageResource(R.drawable.female_select);
            imgMaleFemale.setImageResource(R.drawable.malefemale_de_select);
        } else if (propertyDetails.property_category_id == 4) {
            imgMale.setImageResource(R.drawable.male_de_select);
            imgFemale.setImageResource(R.drawable.female_de_select);
            imgMaleFemale.setImageResource(R.drawable.malefemale_select);
        }
    }

    private void setImagePager() {
        if (propertyDetails.productImages == null || propertyDetails.productImages.isEmpty()) {
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add("");
            propertyDetails.productImages = arrayList;
        }
        DetailImagePagerAdapter mDashboardPagerAdapter = new DetailImagePagerAdapter(this, propertyDetails.productImages);
        vpImage.setAdapter(mDashboardPagerAdapter);

       /* vpImage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == sliderImagesModel.sliderImagesDetail.size()) {
                    currentPage = 0;
                }
                vpImage.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled

            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);
*/
    }

    @OnClick(R.id.tv_bookmark)
    public void onTvBookmarkClicked() {
        doAddBookmark();
    }

    public void doAddBookmark() {
        JSONObject postData = HttpRequestHandler.getInstance().getAddBookmarkParam(property_id);

        if (postData != null) {

            new PostRequest(this, getString(R.string.addBookmark), postData, true, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    PropertyDetailModel propertyDetailModel = new Gson().fromJson(response.toString(), PropertyDetailModel.class);
                    /*if (propertyDetailModel.status == 0) {
                        propertyDetails = propertyDetailModel.propertyDetails;
                        setPropertyDetials();
                    }*/
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


    @OnClick(R.id.tv_review)
    public void onTvReviewClicked() {
        Toaster.shortToast("Review");
    }

    @OnClick(R.id.tv_rate)
    public void onTvRateClicked() {
        Toaster.shortToast("Rate");
    }

    @OnClick(R.id.img_back)
    public void onImgBackClicked() {
        onBackPressed();
    }

    @OnClick(R.id.img_share)
    public void onImgShareClicked() {
        Toaster.shortToast("Share");
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @OnClick(R.id.btn_book_now)
    public void onViewClicked() {
        Toaster.shortToast("Book Now");
    }
}
