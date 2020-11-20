package com.hostelbasera.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.hostelbasera.R;
import com.hostelbasera.apis.HttpRequestHandler;
import com.hostelbasera.apis.PostRequest;
import com.hostelbasera.apis.PostWithRequestParam;
import com.hostelbasera.model.AddImageAttachmentModel;
import com.hostelbasera.model.DocumentUploadModel;
import com.hostelbasera.model.PropertyDetailModel;
import com.hostelbasera.model.PropertyReviewModel;
import com.hostelbasera.seller.AdapterAddAttachment;
import com.hostelbasera.utility.BaseActivity;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.DetailImagePagerAdapter;
import com.hostelbasera.utility.Globals;
import com.hostelbasera.utility.Toaster;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.slybeaver.slycalendarview.SlyCalendarDialog;

import static com.hostelbasera.utility.Constant.Days15;
import static com.hostelbasera.utility.Constant.Monthly;
import static com.hostelbasera.utility.Globals.checkFileSize;

public class HostelDetailActivity extends BaseActivity implements RatingDialogListener, OnMapReadyCallback, PermissionListener {

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
    AppCompatTextView tvBookmark;
    @BindView(R.id.tv_review_rating)
    TextView tvReview;
    @BindView(R.id.tv_enquiry)
    TextView tvEnquiry;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.img_share)
    ImageView imgShare;
    int property_id;

    @BindView(R.id.tv_verified_reviews)
    TextView tvVerifiedReviews;
    @BindView(R.id.rv_review)
    RecyclerView rvReview;
    @BindView(R.id.fl_review)
    FrameLayout flReview;
    @BindView(R.id.cv_price)
    CardView cvPrice;
    @BindView(R.id.cv_rooms)
    CardView cvRooms;
    @BindView(R.id.rv_room_price)
    RecyclerView rvRoomPrice;
    Globals globals;
    @BindView(R.id.tv_rooms)
    TextView tvRooms;

    @BindView(R.id.cv_description)
    CardView cvDescription;
    @BindView(R.id.tv_description)
    TextView tvDescription;

    @BindView(R.id.tv_hostel_pg_for)
    TextView tvHostelPgFor;

    @BindView(R.id.cv_booking_dates)
    CardView cvBookingDates;
    @BindView(R.id.tv_booking_dates)
    TextView tvBookingDates;

    @BindView(R.id.cv_document)
    CardView cvDocument;
    @BindView(R.id.rv_document)
    RecyclerView rvDocument;
    @BindView(R.id.tv_deposit_txt)
    TextView tvDepositTxt;
    @BindView(R.id.tv_notice_period_txt)
    TextView tvNoticePeriodTxt;

    ArrayList<String> arrFile;
    AdapterAddAttachment adapterAddAttachment;
    ArrayList<AddImageAttachmentModel> arrAddImageAttachment;

    PropertyDetailModel.PropertyDetails propertyDetails;
    Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.
    AdapterAmenities adapterAmenities;
    AdapterReview adapterReview;
    @BindView(R.id.btn_book_now)
    Button btnBookNow;
    boolean is_bookmark_remove;

    AdapterRoom adapterRoom;

    int room_id = 0;

    Calendar calStart, calEnd;
//    Date expDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostel_detail);
        ButterKnife.bind(this);
        globals = ((Globals) getApplicationContext());
        init();
    }

    private void init() {
        property_id = getIntent().getIntExtra(Constant.Property_id, 0);
        toolbarTitle.setText(getIntent().getStringExtra(Constant.Property_name));
        imgBack.setVisibility(View.VISIBLE);
        imgShare.setVisibility(View.VISIBLE);
        Globals.doBoldTextView(btnBookNow);
        arrAddImageAttachment = new ArrayList<>();
        Globals.doBoldTextView(tvDepositTxt);

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
                        setPropertyDetails();
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
    private void setPropertyDetails() {
        tvAddress.setText(propertyDetails.address);
        tvTypeOfProperty.setText(propertyDetails.property_type);
        tvSizeOfProperty.setText(propertyDetails.property_size);

        if (propertyDetails.propertyrooms != null && !propertyDetails.propertyrooms.isEmpty()) {
            cvPrice.setVisibility(View.GONE);
            cvRooms.setVisibility(View.VISIBLE);
            tvRooms.setTypeface(tvRooms.getTypeface(), Typeface.BOLD);
            propertyDetails.propertyrooms.get(0).isSelected = true;
            setRoomAdapter();
        } else {
            if (propertyDetails.price != null && !propertyDetails.price.isEmpty() && !propertyDetails.price.equals("0")) {
                tvPrice.setText("₹ " + propertyDetails.price);
            } else {
                btnBookNow.setVisibility(View.GONE);
            }
        }

        is_bookmark_remove = propertyDetails.isBookMark;
        tintViewDrawable();
        if (propertyDetails.propertyReviewDetails != null && !propertyDetails.propertyReviewDetails.isEmpty()) {
            tvVerifiedReviews.setText(propertyDetails.propertyReviewDetails.size() + " Verified Reviews");
            setReViewAdapter();
        } else {
            flReview.setVisibility(View.GONE);
        }
        setHostelFor();
        setImagePager();
        setAmenitiesAdapter();
        if (propertyDetails.latitude != null && !propertyDetails.latitude.isEmpty()) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        if (propertyDetails.description != null && !propertyDetails.description.isEmpty()) {
            cvDescription.setVisibility(View.VISIBLE);
            tvDescription.setText(propertyDetails.description);
        }

        calStart = Calendar.getInstance();
        calStart.setTime(new Date());

        calEnd = Calendar.getInstance();
        calEnd.setTime(new Date());

        switch (propertyDetails.payment_period_id) {
            case Days15:
                calEnd.add(Calendar.DAY_OF_MONTH, 14);
                break;
            case Monthly:
            default:
                calEnd.add(Calendar.MONTH, 1);
                calEnd.add(Calendar.DAY_OF_MONTH, -1);
                break;
        }

//        expDate = calEnd.getTime();

        tvBookingDates.setText(getString(
                R.string.period,
                new SimpleDateFormat(getString(R.string.dateFormat), Locale.getDefault()).format(calStart.getTime()),
                new SimpleDateFormat(getString(R.string.dateFormat), Locale.getDefault()).format(calEnd.getTime())
        ));

        btnBookNow.setVisibility(propertyDetails.booking_status == 1 ? View.VISIBLE : View.GONE);
        cvDocument.setVisibility(propertyDetails.booking_status == 1 ? View.VISIBLE : View.GONE);
        cvBookingDates.setVisibility(propertyDetails.booking_status == 1 ? View.VISIBLE : View.GONE);
//        tvDepositTxt.setVisibility(propertyDetails.booking_status == 1 ? View.VISIBLE : View.GONE);
        tvDepositTxt.setText("Pay deposit (" + Globals.checkString(propertyDetails.deposit_amount) + ") to owner by your own");
//        tvNoticePeriodTxt.setVisibility(propertyDetails.booking_status == 1 ? View.VISIBLE : View.GONE);
        if (!Globals.checkString(propertyDetails.notice_period).isEmpty()) {
            tvNoticePeriodTxt.setVisibility(View.VISIBLE);
            tvNoticePeriodTxt.setText(Globals.checkString(propertyDetails.notice_period));
        } else
            tvNoticePeriodTxt.setVisibility(View.GONE);
    }

    private void setRoomAdapter() {
        if (adapterRoom == null) {
            adapterRoom = new AdapterRoom(this);
        }
        adapterRoom.doRefresh(propertyDetails.propertyrooms);

        if (rvRoomPrice.getAdapter() == null) {
            rvRoomPrice.setHasFixedSize(false);
            rvRoomPrice.setLayoutManager(new LinearLayoutManager(this));
            rvRoomPrice.setItemAnimator(new DefaultItemAnimator());
            rvRoomPrice.setAdapter(adapterRoom);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            LatLng latLng = new LatLng(Double.parseDouble(propertyDetails.latitude), Double.parseDouble(propertyDetails.longitude));
            googleMap.addMarker(new MarkerOptions().position(latLng)
                    .title(propertyDetails.property_name)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.location)));

            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    Context mContext = HostelDetailActivity.this;
                    View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.marker_info_window, null);

                    TextView name_tv = view.findViewById(R.id.tv_title);
                    ImageView img = view.findViewById(R.id.img_hostel);

                    name_tv.setText(marker.getTitle());

                    if (img.getDrawable() == null) {
                        Picasso.get()
                                .load(mContext.getString(R.string.image_url) +
                                        (propertyDetails.productImages != null && !propertyDetails.productImages.isEmpty() ? propertyDetails.productImages.get(0) : ""))
                                .error(R.mipmap.ic_launcher)
                                .into(img, new InfoWindowRefresher(marker));
                    } else {
                        Picasso.get()
                                .load(mContext.getString(R.string.image_url) +
                                        (propertyDetails.productImages != null && !propertyDetails.productImages.isEmpty() ? propertyDetails.productImages.get(0) : ""))
                                .error(R.mipmap.ic_launcher)
                                .into(img);
                    }
                   /* Glide.with(mContext)
                            .load(getString(R.string.image_url) + (propertyDetails.productImages != null && !propertyDetails.productImages.isEmpty() ? propertyDetails.productImages.get(0) : ""))
                            .apply(new RequestOptions()
                                    .fitCenter()
                                    .placeholder(R.mipmap.ic_launcher)
                                    .dontAnimate()
                                    .priority(Priority.HIGH))
                            .into(img);*/


                    return view;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    return null;
                }
            });

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setZoomGesturesEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class InfoWindowRefresher implements Callback {
        private Marker markerToRefresh;

        public InfoWindowRefresher(Marker markerToRefresh) {
            this.markerToRefresh = markerToRefresh;
        }

        @Override
        public void onSuccess() {
            markerToRefresh.showInfoWindow();
        }

        @Override
        public void onError(Exception e) {
            e.printStackTrace();
        }

    }

    private void tintViewDrawable() {
        Drawable[] drawables = tvBookmark.getCompoundDrawables();
        for (Drawable drawable : drawables) {
            if (drawable != null) {
                drawable.setColorFilter(getResources().getColor(is_bookmark_remove ? R.color.transparent : R.color.border_light_gray), PorterDuff.Mode.SRC_ATOP);
            }
        }
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

    private void setReViewAdapter() {
        if (adapterReview == null) {
            adapterReview = new AdapterReview(this, propertyDetails.propertyReviewDetails);
        }
        adapterReview.doRefresh();

        if (rvReview.getAdapter() == null) {
            rvReview.setHasFixedSize(false);
            rvReview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
            rvReview.setItemAnimator(new DefaultItemAnimator());
            rvReview.setAdapter(adapterReview);
        }
    }

    public void setHostelFor() {
        tvHostelPgFor.setText(getString(propertyDetails.type_id == 1 ? R.string.hostel_for : R.string.pg_for));
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
        JSONObject postData = HttpRequestHandler.getInstance().getAddBookmarkParam(property_id, is_bookmark_remove);

        if (postData != null) {

            new PostRequest(this, getString(R.string.addBookmark), postData, true, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    PropertyDetailModel propertyDetailModel = new Gson().fromJson(response.toString(), PropertyDetailModel.class);
                    if (propertyDetailModel.status == 0) {
                        is_bookmark_remove = !is_bookmark_remove;
                        tintViewDrawable();
                    }
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

    @OnClick(R.id.tv_call)
    public void onTvCallClicked() {
        new TedPermission(this)
                .setPermissionListener(this)
                .setRationaleMessage(R.string.call_message)
                .setDeniedMessage(R.string.call_denied_message)
                .setGotoSettingButtonText(R.string.ok)
                .setPermissions(Manifest.permission.CALL_PHONE)
                .check();
    }

    @Override
    public void onPermissionGranted() {
        String contact_no = "+917622885409";//Chintan
        if (propertyDetails != null && propertyDetails.cont_no != null) {
            contact_no = propertyDetails.cont_no.substring(0, 10);
        }

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

    @SuppressLint("SetTextI18n")
    @OnClick(R.id.tv_enquiry)
    public void onTvRateClicked() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.MyEnquiryAlertDialogStyle);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.enquiry_dialog, null);
        dialogBuilder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tv_title);
        EditText edtDescription = dialogView.findViewById(R.id.edt_description);
        TextView tvSubmit = dialogView.findViewById(R.id.tv_submit);
        TextView tvCancel = dialogView.findViewById(R.id.tv_cancel);
        AlertDialog alertDialog = dialogBuilder.create();

        tvTitle.setTypeface(tvTitle.getTypeface(), Typeface.BOLD);
        tvTitle.setText("Enquiry for " + (propertyDetails.property_name != null ? propertyDetails.property_name : ""));
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtDescription.getText().toString().trim().isEmpty()) {
                    Toaster.shortToast("Please write your description here ...");
                    return;
                }
                doAddEnquiry(edtDescription.getText().toString());
                alertDialog.dismiss();
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

    private void doAddEnquiry(String description) {
        if (!Globals.isNetworkAvailable(this)) {
            Toaster.shortToast(R.string.no_internet_msg);
            return;
        }

        JSONObject postData = HttpRequestHandler.getInstance().getAddInquiryDataParam(property_id, description);
        if (postData != null) {

            new PostRequest(this, getString(R.string.addInquiry), postData, true, new PostRequest.OnPostServiceCallListener() {
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


    @OnClick(R.id.tv_review_rating)
    public void onTvReviewClicked() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
//                .setNeutralButtonText("Later")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not Good", "Quite Ok", "Very Good", "Excellent !!!"))
                .setDefaultRating(4)
                .setTitle("Rate this " + propertyDetails.property_name)
                .setDescription("Please select some stars and give your feedback")
                .setDefaultComment("This " + propertyDetails.property_name + " is pretty cool !")
                .setStarColor(R.color.colorAccent)
                .setNoteDescriptionTextColor(R.color.colorPrimary)
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here ...")
                .setHintTextColor(R.color.border_gray)
                .setCommentTextColor(R.color.colorPrimary)
                .setCommentBackgroundColor(R.color.border_light_gray)
//                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .setWindowAnimation(R.style.MyDialogSlideVerticalAnimation)
                .create(HostelDetailActivity.this)
                .show();
    }

    @Override
    public void onNegativeButtonClicked() {
//        Toaster.shortToast("Cancel rating");
    }

    @Override
    public void onNeutralButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int rating, @NonNull String review) {
        if (!Globals.isNetworkAvailable(this)) {
            Toaster.shortToast(R.string.no_internet_msg);
            return;
        }
        JSONObject postData = HttpRequestHandler.getInstance().getAddReviewDataParam(property_id, review, rating);
        if (postData != null) {

            new PostRequest(this, getString(R.string.addReview), postData, true, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    PropertyDetailModel propertyDetailModel = new Gson().fromJson(response.toString(), PropertyDetailModel.class);
                    Toaster.shortToast(propertyDetailModel.message);

                    if (propertyDetailModel.status == 0) {
                        getPropertyReview();
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

    private void getPropertyReview() {
        if (!Globals.isNetworkAvailable(this)) {
            Toaster.shortToast(R.string.no_internet_msg);
            return;
        }
        JSONObject postData = HttpRequestHandler.getInstance().getPropertyReviewDataParam(property_id);
        if (postData != null) {

            new PostRequest(this, getString(R.string.addReview), postData, true, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    PropertyReviewModel propertyReviewModel = new Gson().fromJson(response.toString(), PropertyReviewModel.class);
//                    Toaster.shortToast(propertyReviewDetails.message);

                    if (propertyReviewModel.status == 0 && propertyReviewModel.propertyReviewDetails != null && !propertyReviewModel.propertyReviewDetails.isEmpty()) {
                        propertyDetails.propertyReviewDetails.clear();
                        propertyDetails.propertyReviewDetails = propertyReviewModel.propertyReviewDetails;
                        setReViewAdapter();
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

    @OnClick(R.id.img_back)
    public void onImgBackClicked() {
        onBackPressed();
    }

    @OnClick(R.id.img_share)
    public void onImgShareClicked() {
//        Toaster.shortToast("Share");
       /* if (propertyDetails.productImages == null || propertyDetails.productImages.isEmpty()) {
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add("");
            propertyDetails.productImages = arrayList;
        }*/

        ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setChooserTitle("Share via")
//                .setStream()
                .setText(propertyDetails.property_name +
                        "\nhttp://hostelbasera.com/view/" + propertyDetails.property_name.replaceAll(" ", "-") + "/" + propertyDetails.property_id)
                .startChooser();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @OnClick(R.id.btn_book_now)
    public void onViewClicked() {
        if (!Globals.isNetworkAvailable(this)) {
            Toaster.shortToast(R.string.no_internet_msg);
            return;
        }
        if (arrAddImageAttachment == null || arrAddImageAttachment.isEmpty()) {
            Toaster.shortToast(R.string.please_add_document);
            return;
        }

        arrAddImageAttachment.size();
        setProgressDialog(arrAddImageAttachment.size());

        File[] arr = new File[arrAddImageAttachment.size()];
        for (int i = 0; i < arrAddImageAttachment.size(); i++) {
            arr[i] = new File(arrAddImageAttachment.get(i).FilePath);
        }
        doUploadFile(arr, 0);

    }

    ArrayList<String> arrFileName;
    ArrayList<Integer> arrFileIndex;

    public void setProgressDialog(int size) {
        FileCount = 0;
        arrFileName = new ArrayList<>();
        arrFileIndex = new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setMax(size);
        progressDialog.setTitle("File Uploading");
        progressDialog.show();
    }

    ProgressDialog progressDialog;
    int FileCount = 0;

    private void doUploadFile(File[] file, int index) {
        RequestParams requestParams = new RequestParams();
        try {
            requestParams.put(Constant.Userfile_, file);
            requestParams.put(Constant.File_id, index);
            requestParams.put(Constant.Type, "document");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        new PostWithRequestParam(this, getString(R.string.uploadPropertyimages), requestParams,
                false, new PostWithRequestParam.OnPostWithReqParamServiceCallListener() {
            @Override
            public void onSucceedToPostCall(JSONObject response) {
                progressDialog.setProgress(FileCount);
                FileCount++;
                DocumentUploadModel fileUploadModel = new Gson().fromJson(response.toString(), DocumentUploadModel.class);
                if (fileUploadModel.status == 0 && fileUploadModel.uploadPropertyImagesDetail != null) {
                    for (int i = 0; i < fileUploadModel.uploadPropertyImagesDetail.size(); i++) {
//                        arrFileName.add(fileUploadModel.uploadPropertyImagesDetail.get(i).file_name);
//                        arrFileIndex.add(fileUploadModel.uploadPropertyImagesDetail.get(i).file_id);

                        arrAddImageAttachment.get(i).FileName = fileUploadModel.uploadPropertyImagesDetail.get(i).file_name;
                        arrAddImageAttachment.get(i).uploadPropertyImagesDetail = fileUploadModel.uploadPropertyImagesDetail.get(i);
                    }

                    /*if (arrFileName.size() == arrAddImageAttachment.size()) {
                        int count = 0;
                        for (int j = 0; j < arrAddImageAttachment.size(); j++) {
                            for (int k = 0; k < arrFileIndex.size(); k++) {
                                if (arrFileIndex.get(k) == count) {
                                    arrAddImageAttachment.get(j).FileName = arrFileName.get(k);
                                    arrAddImageAttachment.get(j).uploadPropertyImagesDetail = fileUploadModel.uploadPropertyImagesDetail.get(0);
                                    break;
                                }
                            }
                            count++;
                        }*/
//                        if (dialog != null && dialog.isShowing())
//                            dialog.dismiss();
                    progressDialog.dismiss();
                    doBook();
//                    }
                } else {
//                    isFailToUpload = true;
                    progressDialog.dismiss();
                    Toaster.shortToast(fileUploadModel.message);
                }
            }

            @Override
            public void onFailedToPostCall(int statusCode, String msg) {
//                isFailToUpload = true;
//                if (dialog != null && dialog.isShowing())
//                    dialog.dismiss();
                progressDialog.dismiss();
                Toaster.shortToast(msg);
            }

            @Override
            public void onProgressCall(int progress) {
                progressDialog.setProgress(progress);
            }
        }).execute();
    }

    public void doBook() {
        if (adapterRoom != null) {
            room_id = adapterRoom.room_id;
        }

        JSONObject postData = HttpRequestHandler.getInstance().getBookNowDataParam(property_id, room_id, propertyDetails.seller_id,
                new SimpleDateFormat(getString(R.string.UploadDateFormat), Locale.getDefault()).format(calStart.getTime()),
                new SimpleDateFormat(getString(R.string.UploadDateFormat), Locale.getDefault()).format(calEnd.getTime()),
                arrAddImageAttachment);
        if (postData != null) {

            new PostRequest(this, getString(R.string.bookNow), postData, true, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    PropertyReviewModel propertyReviewModel = new Gson().fromJson(response.toString(), PropertyReviewModel.class);
                    Toaster.shortToast(propertyReviewModel.message);

                    if (propertyReviewModel.status == 0) {
                        finish();
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


    @OnClick(R.id.cv_booking_dates)
    public void onCvBookingDatesClicked() {
        new SlyCalendarDialog()
                .setSingle(false)
                .setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                .setSelectedTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setSelectedColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setHeaderColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setStartDate(calStart.getTime())
                .setEndDate(calEnd.getTime())
                .setFirstMonday(false)
                .setCallback(new SlyCalendarDialog.Callback() {
                    @Override
                    public void onCancelled() {
                    }

                    @Override
                    public void onDataSelected(Calendar firstDate, Calendar secondDate, int hours, int minutes) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.dateFormat), Locale.getDefault());
                        if (firstDate != null && secondDate != null) {

                            if (doValidateDaysMonth(dateFormat, firstDate, secondDate)) {
                                calStart = firstDate;
                                calEnd = secondDate;
//                                Toaster.shortToast(getString(
//                                        R.string.period,
//                                        dateFormat.format(firstDate.getTime()),
//                                        dateFormat.format(secondDate.getTime())
//                                ));

                                tvBookingDates.setText(getString(
                                        R.string.period,
                                        dateFormat.format(firstDate.getTime()),
                                        dateFormat.format(secondDate.getTime())
                                ));
                            } else {
                                Toaster.shortToast("Please select " + (propertyDetails.payment_period_id == Days15 ? "15 Days" : "1 Month") + " from both dates");
                            }
                        } else {
                            Toaster.shortToast("Please select both dates");
                        }
                    }
                })
                .show(getSupportFragmentManager(), "TAG_SLYCALENDAR");
    }

    public boolean doValidateDaysMonth(SimpleDateFormat dateFormat, Calendar firstDate, Calendar secondDate) {
        switch (propertyDetails.payment_period_id) {
            case Days15: //TODO : Check This Validation Proper
                if (getDateDiff(dateFormat, dateFormat.format(firstDate.getTime()), dateFormat.format(secondDate.getTime())) == 15) {
                    return false;
                }
                break;
            case Monthly:
            default:
                Calendar cal = Calendar.getInstance();
                cal.setTime(firstDate.getTime());
                cal.add(Calendar.MONTH, 1);
                cal.add(Calendar.DAY_OF_MONTH, -1);
                return secondDate.getTime().toString().equals(cal.getTime().toString());
        }
        return true;
    }


    public long getDateDiff(SimpleDateFormat format, String oldDate, String newDate) {
        try { //TODO : Check This Validation Proper
            return TimeUnit.DAYS.convert(format.parse(newDate).getTime() - format.parse(oldDate).getTime(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @OnClick(R.id.tv_add_doc)
    public void doAddDocument() {
        ImagePicker.create(this)
                .showCamera(true)
                .theme(R.style.ImagePickerTheme)
                .limit(10)
                .folderMode(true)
                .includeVideo(false)
                .toolbarFolderTitle(getString(R.string.select_image))
                .start();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // Get a list of picked images
            List<Image> images = ImagePicker.getImages(data);

            for (int i = 0; i < images.size(); i++) {
                arrFile = new ArrayList<>();
                arrFile.add(images.get(i).getPath());
                doAttachment();
            }
        }


      /* if (requestCode == PAYMENT_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                if (data.getStringExtra(Constant.RESULT).equals("Success")) {
                    paymentId = data.getStringExtra(Constant.Payment_id);

                    if (arrAddImageAttachment.size() > 0) {
                        setProgressDialog(arrAddImageAttachment.size());
                        for (int i = 0; i < arrAddImageAttachment.size(); i++) {
                            doUploadFile(new File(arrAddImageAttachment.get(i).FilePath), i);
                        }
                    } else if (arrAddMenuAttachment.size() > 0) {
                        setProgressDialog(arrAddMenuAttachment.size());
                        for (int i = 0; i < arrAddMenuAttachment.size(); i++) {
                            doUploadMenuFile(new File(arrAddMenuAttachment.get(i).FilePath), i);
                        }
                    } else {
                        doAddHostelPG();
                    }
                }

            }
        }*/

    }


    public void doAttachment() {
        for (int i = 0; i < arrFile.size(); i++) {
            if (checkFileSize(arrFile.get(i))) {
                Toaster.shortToast(getString(R.string.Max_10mb_file_allowed));
                return;
            }
        }
        for (int i = 0; i < arrFile.size(); i++) {
            AddImageAttachmentModel addImageAttachmentModel = new AddImageAttachmentModel();
            addImageAttachmentModel.FilePath = arrFile.get(i);
            arrAddImageAttachment.add(addImageAttachmentModel);
        }

        setAttachment();
    }

    public void setAttachment() {
        if (adapterAddAttachment == null) {
            adapterAddAttachment = new AdapterAddAttachment(this);
        }
        adapterAddAttachment.doRefresh(arrAddImageAttachment);

        if (rvDocument.getAdapter() == null) {
            rvDocument.setHasFixedSize(true);
            rvDocument.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rvDocument.setItemAnimator(new DefaultItemAnimator());
            rvDocument.setAdapter(adapterAddAttachment);
        }
    }


}


