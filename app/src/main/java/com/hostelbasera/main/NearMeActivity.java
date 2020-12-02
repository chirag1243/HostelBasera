package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.hostelbasera.R;
import com.hostelbasera.apis.HttpRequestHandler;
import com.hostelbasera.apis.PostRequest;
import com.hostelbasera.model.FilterModel;
import com.hostelbasera.model.GetPropertyDetailModel;
import com.hostelbasera.utility.BaseActivity;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.Globals;
import com.hostelbasera.utility.Toaster;
import com.squareup.picasso.Callback;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NearMeActivity extends BaseActivity implements OnMapReadyCallback {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.img_back)
    ImageView imgBack;

    @BindView(R.id.rv_hostel_list)
    RecyclerView rvHostelList;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tv_no_data_found)
    TextView tvNoDataFound;

    Globals globals;

    int pageNo = 1;

    GetPropertyDetailModel getPropertyDetailModel;
    ArrayList<GetPropertyDetailModel.PropertyDetail> arrPropertyDetailArrayList;

    AdapterCategoryList adapterCategoryList;

    ArrayList<String> arrPropertyCategoryId;
    ArrayList<String> arrPropertyTypeId;
    ArrayList<String> arrTypeId;
    ArrayList<String> arrPropertySizeId;
    ArrayList<String> arrPriceId;
    FilterModel filterModel;
    private static final int filterCode = 1005;
    double latitude = 0, longitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_me);
        ButterKnife.bind(this);
        init();
    }


    @SuppressLint("SetTextI18n")
    private void init() {
        globals = ((Globals) this.getApplicationContext());
        progressBar.setVisibility(View.GONE);
        toolbarTitle.setText(getIntent().getStringExtra(Constant.Category_name));

        imgBack.setVisibility(View.VISIBLE);

        arrPropertyDetailArrayList = new ArrayList<>();
        tvNoDataFound.setText("");

        arrPropertyTypeId = new ArrayList<>();
        arrTypeId = new ArrayList<>();
        arrPriceId = new ArrayList<>();
        arrPropertySizeId = new ArrayList<>();
        arrPropertyCategoryId = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (getIntent().hasExtra(Constant.ArrPropertyCategoryId)) {
            arrPropertyCategoryId = getIntent().getStringArrayListExtra(Constant.ArrPropertyCategoryId);
        }

        if (getIntent().hasExtra(Constant.Latitude) && getIntent().hasExtra(Constant.Longitude)) {
            latitude = getIntent().getDoubleExtra(Constant.Latitude, 0);
            longitude = getIntent().getDoubleExtra(Constant.Longitude, 0);
        }

        if (Globals.isNetworkAvailable(this)) {
            getPropertyListData(true, false);
            getFiltersData();
        } else {
            showNoRecordFound(getString(R.string.no_data_found));
            Toaster.shortToast(R.string.no_internet_msg);
        }

        /*
        latitude = 23.010353;
        longitude = 72.5054966;
        String url = getUrl(latitude, longitude, "bus_station");
        Object[] DataTransfer = new Object[2];
//        DataTransfer[0] = mMap;
        DataTransfer[1] = url;
        Log.d("onClick", url);
        new GetNearbyPlacesData(this).execute(DataTransfer);

         */

    }

    private int PROXIMITY_RADIUS = 10000;

    private String getUrl(double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyATuUiZUkEc_UgHuqsBJa1oqaODI-3mLs0");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }


    public void getPropertyListData(boolean showProgress, boolean isFilter) {
        JSONObject postData;
//        //TODO :Remove
//        latitude = 23.010353;
//        longitude = 72.5054966;
//        23.0226819
//        72.5797763
        postData = HttpRequestHandler.getInstance().getNearbyPropertyDataParam(pageNo, arrPropertyCategoryId, arrPropertyTypeId, arrTypeId, arrPropertySizeId, arrPriceId, latitude, longitude);//23.010336, 72.505890);

        if (postData != null) {

            new PostRequest(this, getString(R.string.getNearbyProperty), postData, showProgress, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    if (progressBar.getVisibility() == View.VISIBLE)
                        progressBar.setVisibility(View.GONE);

                    getPropertyDetailModel = new Gson().fromJson(response.toString(), GetPropertyDetailModel.class);
                    if (getPropertyDetailModel.propertyDetail != null && !getPropertyDetailModel.propertyDetail.isEmpty()) {
                        if (isFilter) {
                            arrPropertyDetailArrayList = new ArrayList<>();
                        }
                        setupList(getPropertyDetailModel.propertyDetail);
                    } else {
                        if (pageNo == 1) {
                            showNoRecordFound("");
                            Toaster.shortToast(getPropertyDetailModel.message);
                        }
                    }
                }

                @Override
                public void onFailedToPostCall(int statusCode, String msg) {
                    if (progressBar.getVisibility() == View.VISIBLE)
                        progressBar.setVisibility(View.GONE);
                    Toaster.shortToast(msg);
                }
            }).execute();
        }
        Globals.hideKeyboard(this);
    }

    private void setupList(ArrayList<GetPropertyDetailModel.PropertyDetail> homePageStoresDetailArrayList) {
        if (homePageStoresDetailArrayList != null && !homePageStoresDetailArrayList.isEmpty()) {
            hideNoRecordFound();
            arrPropertyDetailArrayList.addAll(homePageStoresDetailArrayList);
            setAllMarkers();
            setAdapter();
        } else
            showNoRecordFound(getString(R.string.no_data_found));
    }


    private void setAdapter() {

        if (adapterCategoryList == null) {
            adapterCategoryList = new AdapterCategoryList(this);
        }
        adapterCategoryList.doRefresh(arrPropertyDetailArrayList, true, false);

        if (rvHostelList.getAdapter() == null) {
            rvHostelList.setHasFixedSize(false);
            rvHostelList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rvHostelList.setItemAnimator(new DefaultItemAnimator());
            rvHostelList.setAdapter(adapterCategoryList);

            rvHostelList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        onPageChanged(getCurrentItem());
                    }
                }
            });
            PagerSnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(rvHostelList);
        }

        adapterCategoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(NearMeActivity.this, HostelDetailActivity.class)
                        .putExtra(Constant.Property_id, arrPropertyDetailArrayList.get(position).property_id)
                        .putExtra(Constant.Property_name, arrPropertyDetailArrayList.get(position).property_name));
            }
        });
        onPageChanged(0);
    }

    ArrayList<Marker> markerArrayList;
    int oldPosition = 0;

    private void onPageChanged(int position) {
        if (arrPropertyDetailArrayList.get(position).latitude != null && !arrPropertyDetailArrayList.get(position).latitude.isEmpty()
                && arrPropertyDetailArrayList.get(position).longitude != null && !arrPropertyDetailArrayList.get(position).longitude.isEmpty()) {

            LatLng latLng = new LatLng(Double.parseDouble(arrPropertyDetailArrayList.get(position).latitude), Double.parseDouble(arrPropertyDetailArrayList.get(position).longitude));

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(16).build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);

            markerArrayList.get(oldPosition).hideInfoWindow();

            oldPosition = position;
            markerArrayList.get(position).showInfoWindow();

            /*googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    Context mContext = NearMeActivity.this;
                    View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.marker_info_title_window, null);

                    TextView name_tv = view.findViewById(R.id.tv_title);
                    name_tv.setText(marker.getTitle());

                    return view;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    return null;
                }
            });*/
//            Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng).title(arrPropertyDetailArrayList.get(position).property_name));
//            marker.showInfoWindow();

        }
    }

    private int getCurrentItem() {
        return ((LinearLayoutManager) rvHostelList.getLayoutManager())
                .findFirstVisibleItemPosition() + 1;
    }

    @OnClick(R.id.img_back)
    public void onImgBackClicked() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    public void getFiltersData() {
        JSONObject postData = HttpRequestHandler.getInstance().getFilterListParam();

        if (postData != null) {
            new PostRequest(this, getString(R.string.getFilterList),
                    postData, false, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    filterModel = new Gson().fromJson(response.toString(), FilterModel.class);
                    /*if (filterModel.status == 0 && filterModel.filtersOfFullCatalogsDetail != null) {
                    } else {
                        Toaster.shortToast(filterModel.message);
                    }*/
                }

                @Override
                public void onFailedToPostCall(int statusCode, String msg) {
                    Toaster.shortToast(msg);
                }
            }).execute();
        }
        Globals.hideKeyboard(this);
    }

    private void showNoRecordFound(String no_data_found) {
        rvHostelList.setVisibility(View.GONE);
        if (tvNoDataFound.getVisibility() == View.GONE) {
            tvNoDataFound.setVisibility(View.VISIBLE);
            tvNoDataFound.setText(no_data_found);
        }
    }

    private void hideNoRecordFound() {
        rvHostelList.setVisibility(View.VISIBLE);
        if (tvNoDataFound.getVisibility() == View.VISIBLE)
            tvNoDataFound.setVisibility(View.GONE);
    }

    GoogleMap googleMap;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {

            this.googleMap = googleMap;
            LatLng latLng = new LatLng(latitude, longitude);
            googleMap.addMarker(new MarkerOptions().position(latLng)
                    .title("My Location")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.near)));

            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    Context mContext = NearMeActivity.this;
                    View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.marker_info_title_window, null);

                    TextView name_tv = view.findViewById(R.id.tv_title);
                    name_tv.setText(marker.getTitle());

                    /*if (img.getDrawable() == null) {
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
                    }*/

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

    private int getMarkerPosition(Marker marker) {
        for (int i = 0; i < markerArrayList.size(); i++) {
            if (markerArrayList.get(i).getTitle().equals(marker.getTitle())) {
                return i;
            }
        }
        return 0;
    }

    private void setAllMarkers() {

        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        markerArrayList = new ArrayList<>();

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                rvHostelList.getLayoutManager().scrollToPosition(getMarkerPosition(marker));
                return false;
            }
        });

        for (int i = 0; i < arrPropertyDetailArrayList.size(); i++) {
            if (arrPropertyDetailArrayList.get(i).latitude != null && !arrPropertyDetailArrayList.get(i).latitude.isEmpty() && arrPropertyDetailArrayList.get(i).longitude != null && !arrPropertyDetailArrayList.get(i).longitude.isEmpty()) {

                LatLng latLng = new LatLng(Double.parseDouble(arrPropertyDetailArrayList.get(i).latitude), Double.parseDouble(arrPropertyDetailArrayList.get(i).longitude));

                MarkerOptions pin = new MarkerOptions();
                pin.position(latLng);
                pin.title("₹ " + arrPropertyDetailArrayList.get(i).price);
                pin.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_logo));

                Marker marker = googleMap.addMarker(pin);

                markerArrayList.add(marker);

//                googleMap.addMarker(new MarkerOptions().position(latLng)
//                        .title(arrPropertyDetailArrayList.get(i).property_name)
////                        .snippet(arrPropertyDetailArrayList.get(i).image)
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_logo)));

                /*googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        Context mContext = NearMeActivity.this;
                        View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.marker_info_window, null);

                        TextView name_tv = view.findViewById(R.id.tv_title);
                        ImageView img = view.findViewById(R.id.img_hostel);

                        name_tv.setText(marker.getTitle());

                        if (img.getDrawable() == null) {
                            Picasso.get()
                                    .load(mContext.getString(R.string.image_url) + marker.getSnippet())
                                    .error(R.mipmap.ic_launcher)
                                    .into(img, new InfoWindowRefresher(marker));
                        } else {
                            Picasso.get()
                                    .load(mContext.getString(R.string.image_url) + marker.getSnippet())
                                    .error(R.mipmap.ic_launcher)
                                    .into(img);
                        }

                        return view;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        return null;
                    }
                });*/

//                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//
//                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
//
//                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//
//                googleMap.getUiSettings().setZoomControlsEnabled(true);
//                googleMap.getUiSettings().setZoomGesturesEnabled(false);
            }
        }
    }
}