package com.hostelbasera.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.easywaylocation.EasyWayLocation;
import com.example.easywaylocation.Listener;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.hostelbasera.R;
import com.hostelbasera.apis.HttpRequestHandler;
import com.hostelbasera.apis.PostRequest;
import com.hostelbasera.model.BannerListModel;
import com.hostelbasera.model.FilterModel;
import com.hostelbasera.model.GetPropertyDetailModel;
import com.hostelbasera.utility.BannerImagePagerAdapter;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.Globals;
import com.hostelbasera.utility.PaginationProgressBarAdapter;
import com.hostelbasera.utility.Toaster;
import com.paginate.Paginate;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FragmentHome extends Fragment implements Paginate.Callbacks, SwipeRefreshLayout.OnRefreshListener, PermissionListener, Listener {

    Globals globals;
    View view;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.tv_girls)
    TextView tvGirls;
    @BindView(R.id.tv_boys)
    TextView tvBoys;
    @BindView(R.id.tv_both)
    TextView tBoth;
    @BindView(R.id.tv_near_me)
    TextView tvNearMe;
    //    @BindView(R.id.tvHostelSuggestion)
//    TextView tvHostelSuggestion;
    @BindView(R.id.rv_hostel)
    RecyclerView rvHostel;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.rotate_loading)
    RotateLoading rotateLoading;
    @BindView(R.id.img_icon)
    ImageView imgIcon;
    @BindView(R.id.fl_rotate_loading)
    FrameLayout flRotateLoading;
    @BindView(R.id.tv_no_data_found)
    TextView tvNoDataFound;
//    @BindView(R.id.ll_hostel)
//    LinearLayout llHostel;

    @BindView(R.id.fa_button)
    FloatingActionButton faButton;

    //    @BindView(R.id.tv_hostel_suggestion)
//    TextView tvHostelSuggestion;
//    @BindView(R.id.rv_banner)
//    RecyclerView rvBanner;
    @BindView(R.id.fl_banner)
    FrameLayout flBanner;

    @BindView(R.id.vp_banner)
    ViewPager vpBanner;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    ArrayList<String> arrPropertyCategoryId;
    ArrayList<String> arrPropertyTypeId;
    ArrayList<String> arrTypeId;
    ArrayList<String> arrPropertySizeId;
    ArrayList<String> arrPriceId;
    FilterModel filterModel;
    BannerListModel bannerListModel;

    private static final int filterCode = 1005;

    private Paginate paginate;
    private boolean loading = false;

    //   DashboardPagerAdapter mDashboardPagerAdapter;
    int currentPage = 0;
    int pageNo = 1;
    Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.

    GetPropertyDetailModel getPropertyDetailModel;

    ArrayList<GetPropertyDetailModel.PropertyDetail> arrPropertyDetailArrayList;
    AdapterHomePropertyDetail adapterHomePropertyDetail;
    DashboardActivity activity;

    EasyWayLocation easyWayLocation;
    //    private double lati = 0, longi = 0;
    boolean isNearMe;
    Context mContext;

    public static FragmentHome newInstance(/*AllCategoriesDetailModel model*/) {
        FragmentHome fragment = new FragmentHome();
//        fragment.allCategoriesModel = model;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        mContext = container.getContext();
        animShow = AnimationUtils.loadAnimation(mContext, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation(mContext, R.anim.view_hide);

        init();
        return view;
    }


    ArrayList<String> arrCategoryId = new ArrayList<>();

    @OnClick(R.id.tv_girls)
    public void onTvGirlsClicked() {
        doRedirectCategoryList("2", getString(R.string.girls));
    }

    @OnClick(R.id.tv_boys)
    public void onTvBoysClicked() {
        doRedirectCategoryList("1", getString(R.string.boys));
    }

    @OnClick(R.id.tv_both)
    public void onTBothClicked() {
        doRedirectCategoryList("4", getString(R.string.both));
    }

    public void doRedirectCategoryList(String categoryId, String category_name) {
        arrCategoryId.clear();
        arrCategoryId.add(categoryId);
        startActivity(new Intent(getActivity(), CategoryListActivity.class).putExtra(Constant.ArrPropertyCategoryId, arrCategoryId)
                .putExtra(Constant.Category_name, category_name));
    }

    @OnClick(R.id.tv_near_me)
    public void onTvNearMeClicked() {
        isNearMe = true;
        new TedPermission(getActivity())
                .setPermissionListener(this)
                .setRationaleMessage(R.string.location_message)
                .setDeniedMessage(R.string.location_denied_message)
                .setGotoSettingButtonText(R.string.ok)
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();
    }

    @Override
    public void onPermissionGranted() {
//        Toaster.shortToast("Location permission granted");
        easyWayLocation = new EasyWayLocation(mContext);
        easyWayLocation.setListener(this);

        if (easyWayLocation != null)
            easyWayLocation.beginUpdates();

        if (isNearMe) {
            if (globals.getLatitude() != 0 && globals.getLongitude() != 0) {
                startActivity(new Intent(getActivity(), NearMeActivity.class)
                        .putExtra(Constant.Category_name, getString(R.string.near_me))
                        .putExtra(Constant.Latitude, globals.getLatitude())
                        .putExtra(Constant.Longitude, globals.getLongitude()));
            } else {
                isNearMe = false;
                Toaster.shortToast("Not getting your Location.");
            }
        }
    }

    @Override
    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
        Toaster.shortToast("Location permission denied");
        isNearMe = false;
    }

    public Animation animShow, animHide;

    @SuppressLint("SetTextI18n")
    private void init() {

        Globals.hideKeyboard(getActivity());
        globals = ((Globals) getActivity().getApplicationContext());
        activity = (DashboardActivity) getActivity();
        progressBar.setVisibility(View.GONE);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(this);

        arrPropertyDetailArrayList = new ArrayList<>();
        tvNoDataFound.setText("");

        arrPropertyTypeId = new ArrayList<>();
        arrTypeId = new ArrayList<>();
        arrPriceId = new ArrayList<>();
        arrPropertySizeId = new ArrayList<>();
        arrPropertyCategoryId = new ArrayList<>();

        isNearMe = false;
        new TedPermission(getActivity())
                .setPermissionListener(this)
                .setRationaleMessage(R.string.location_message)
                .setDeniedMessage(R.string.location_denied_message)
                .setGotoSettingButtonText(R.string.ok)
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .check();

        if (Globals.isNetworkAvailable(getActivity())) {
            getBannerListData();
            getPropertyListData(true, false);
            getFiltersData();
        } else {
            showNoRecordFound(getString(R.string.no_data_found));
            Toaster.shortToast(R.string.no_internet_msg);
        }

        rvHostel.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (rvHostel.getChildAt(0) != null) {
                    swipeRefreshLayout.setEnabled(rvHostel.getChildAt(0).getTop() == 0);
                    showHideAnimation(rvHostel.getChildAt(0).getTop() == 0);
//                    if (rvHostel.getChildAt(0).getTop() == 0) {
//                        slideDown(rvBanner);
//                    } else {
//                        slideUp(rvBanner);
//                    }
//                    showHideAnimation(rvHostel.getChildAt(0).getTop() == 0);
                }
                if (dy > 0 && faButton.getVisibility() == View.VISIBLE) {
                    faButton.hide();
                } else if (dy < 0 && faButton.getVisibility() != View.VISIBLE) {
                    faButton.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        /*nsScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView nestedScrollView, int i, int i1, int i2, int i3) {
                if (nsScroll.getChildAt(0) != null) {
                    swipeRefreshLayout.setEnabled(nsScroll.getChildAt(0).getTop() == 0);
                }
                if (i > 0 && faButton.getVisibility() == View.VISIBLE) {
                    faButton.hide();
                } else if (i < 0 && faButton.getVisibility() != View.VISIBLE) {
                    faButton.show();
                }
            }
        });*/


        /*nsScroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if(v.getChildAt(v.getChildCount() - 1) != null) {
                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                        scrollY > oldScrollY) {
                    //code to fetch more data for endless scrolling
                }
            }
        });*/
    }

    public void slideUp(View view) {
//        TranslateAnimation animate = new TranslateAnimation(
//                0,                 // fromXDelta
//                0,                 // toXDelta
//                view.getHeight(),  // fromYDelta
//                0);                // toYDelta
//        animate.setDuration(500);
//        animate.setFillAfter(true);
//        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    // slide the view from its current position to below itself
    public void slideDown(View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);

    }

    public void showHideAnimation(boolean isShow) {
        if (isShow) {
            flBanner.setVisibility(View.VISIBLE);
            flBanner.startAnimation(animShow);
//            rvBanner.setVisibility(View.VISIBLE);
//            rvBanner.setAlpha(0.0f);
//
//// Start the animation
//            rvBanner.animate()
//                    .translationY(rvBanner.getHeight())
//                    .alpha(1.0f)
//                    .setListener(null);
        } else {
            flBanner.startAnimation(animHide);
            flBanner.setVisibility(View.GONE);
//            rvBanner.animate()
//                    .translationY(0)
//                    .alpha(0.0f)
//                    .setListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            super.onAnimationEnd(animation);
//                            rvBanner.setVisibility(View.GONE);
//                        }
//                    });
        }
    }

    @OnClick(R.id.tv_search)
    public void onSearchClick() {
        startActivity(new Intent(getActivity(), SearchActivity.class));
    }

    private void showNoRecordFound(String no_data_found) {
        loading = false;
        rvHostel.setVisibility(View.GONE);
        if (tvNoDataFound.getVisibility() == View.GONE) {
            tvNoDataFound.setVisibility(View.VISIBLE);
            tvNoDataFound.setText(no_data_found);
        }
    }

    private void hideNoRecordFound() {
        rvHostel.setVisibility(View.VISIBLE);
        if (tvNoDataFound.getVisibility() == View.VISIBLE)
            tvNoDataFound.setVisibility(View.GONE);
    }


    public void stopLoader() {
        rotateLoading.stop();
        rotateLoading.setVisibility(View.GONE);
        flRotateLoading.setVisibility(View.GONE);
    }

    public void startLoader() {
        flRotateLoading.setVisibility(View.VISIBLE);
        rotateLoading.setVisibility(View.VISIBLE);
        rotateLoading.start();
        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(10000);
        rotate.setInterpolator(new LinearInterpolator());

        rotate.setRepeatCount(Animation.INFINITE);
        imgIcon.startAnimation(rotate);
    }

    public void getFiltersData() {
        JSONObject postData = HttpRequestHandler.getInstance().getFilterListParam();

        if (postData != null) {
            new PostRequest(getActivity(), getString(R.string.getFilterList),
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
        Globals.hideKeyboard(getActivity());
    }

    public void getBannerListData() {
        JSONObject postData = HttpRequestHandler.getInstance().getFilterListParam();

        if (postData != null) {
            new PostRequest(Objects.requireNonNull(getActivity()), getString(R.string.bannerList),
                    postData, false, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    bannerListModel = new Gson().fromJson(response.toString(), BannerListModel.class);
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
        Globals.hideKeyboard(getActivity());
    }

    @OnClick(R.id.fa_button)//ll_filter
    public void onLlFilterClicked() {
        if (filterModel != null) {
            if (filterModel.status == 0 && filterModel.filterDetail != null) {
                startActivityForResult(new Intent(getActivity(), FilterActivity.class).putExtra(Constant.FilterModel, filterModel), filterCode);
            } else {
                Toaster.shortToast(filterModel.message);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == filterCode) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    filterModel = (FilterModel) data.getSerializableExtra(Constant.FilterModel);
                    doGetFilterData(data.getBooleanExtra(Constant.IsClearAll, false));
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void doGetFilterData(boolean isClearAll) {
        arrPropertyTypeId.clear();
        arrPropertySizeId.clear();
        arrTypeId.clear();
        arrPriceId.clear();

        if (!isClearAll) {
            for (int i = 0; i < filterModel.filterDetail.size(); i++) {
                for (int j = 0; j < filterModel.filterDetail.get(i).data.size(); j++) {

                    if (filterModel.filterDetail.get(i).data.get(j).isSelected) {
                        switch (filterModel.filterDetail.get(i).filterType) {
                            case Constant.Size:
                                arrPropertySizeId.add("" + filterModel.filterDetail.get(i).data.get(j).property_size_id);
                                break;
                            case Constant.Property_Types:
                                arrPropertyTypeId.add("" + filterModel.filterDetail.get(i).data.get(j).property_type_id);
                                break;
                            case Constant.Types:
                                arrTypeId.add("" + filterModel.filterDetail.get(i).data.get(j).type_id);
                                break;
                            case Constant.Prices:
                                arrPriceId.add("" + filterModel.filterDetail.get(i).data.get(j).id);
                                break;
                        }
                    }
                }
            }
        }
//        if (adapterCategoryList != null) {
//            arrPropertyDetailArrayList = new ArrayList<>();
//            adapterCategoryList.doRefresh(arrPropertyDetailArrayList, isNearMe);
//        }
        pageNo = 1;
        getPropertyListData(true, true);
    }


    public void getPropertyListData(boolean showProgress, boolean isFilter) {
        JSONObject postData = HttpRequestHandler.getInstance().getPropertyListDataParam(pageNo, arrPropertyCategoryId, arrPropertyTypeId, arrTypeId, arrPropertySizeId, arrPriceId);

        if (postData != null) {
            if (!swipeRefreshLayout.isRefreshing() && showProgress)
                progressBar.setVisibility(View.VISIBLE);
//            startLoader();

            new PostRequest(getActivity(), getString(R.string.getPropertyList),
                    postData, isFilter, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    if (progressBar.getVisibility() == View.VISIBLE)
                        progressBar.setVisibility(View.GONE);
//                    stopLoader();
                    getPropertyDetailModel = new Gson().fromJson(response.toString(), GetPropertyDetailModel.class);
                    if (getPropertyDetailModel.propertyDetail != null && !getPropertyDetailModel.propertyDetail.isEmpty()) {
                        if (swipeRefreshLayout.isRefreshing()) {
                            stopRefreshing();
                            try {
                                rvHostel.setAdapter(null);
                                arrPropertyDetailArrayList.clear();
                                adapterHomePropertyDetail.notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (isFilter) {
                            arrPropertyDetailArrayList = new ArrayList<>();
                        }
                        setupList(getPropertyDetailModel.propertyDetail, showProgress);
                    } else {
                        stopRefreshing();
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
                    if (paginate != null)
                        paginate.unbind();
                    Toaster.shortToast(msg);
                }
            }).execute();
        }
        Globals.hideKeyboard(getActivity());
    }

    private void stopRefreshing() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void setupList
            (ArrayList<GetPropertyDetailModel.PropertyDetail> homePageStoresDetailArrayList,
             boolean showProgress) {
        if (homePageStoresDetailArrayList != null && !homePageStoresDetailArrayList.isEmpty()) {
            arrPropertyDetailArrayList.addAll(homePageStoresDetailArrayList);
            if (showProgress) {
                setRvBanner();
                arrPropertyDetailArrayList.add(0, new GetPropertyDetailModel.PropertyDetail());

//                GetPropertyDetailModel.PropertyDetail model = new GetPropertyDetailModel.PropertyDetail();
//                model.banners = bannerListModel.banners;
//                arrPropertyDetailArrayList.add(0, model);
            }
            setAdapter();
        } else
            showNoRecordFound(getString(R.string.no_data_found));
    }

    public void setRvBanner() {
//        tvHostelSuggestion.setTypeface(tvHostelSuggestion.getTypeface(), Typeface.BOLD);

        if (bannerListModel.banners != null && !bannerListModel.banners.isEmpty()) {

            BannerImagePagerAdapter adapter = new BannerImagePagerAdapter(mContext, bannerListModel.banners);
            vpBanner.setAdapter(adapter);

            tabLayout.setupWithViewPager(vpBanner, true);

            final Handler handler = new Handler();
            final Runnable Update = new Runnable() {
                public void run() {
                    if (currentPage == ((bannerListModel.banners.size() + 1) - 1)) {
                        currentPage = 0;
                    }
                    vpBanner.setCurrentItem(currentPage++, true);
                }
            };

            timer = new Timer(); // This will create a new Thread
            timer.schedule(new TimerTask() { // task to be scheduled
                @Override
                public void run() {
                    handler.post(Update);
                }
            }, DELAY_MS, PERIOD_MS);
/*
            rvBanner.setVisibility(View.VISIBLE);

            AdapterBanners adapterDocuments = new AdapterBanners(mContext);

            rvBanner.setHasFixedSize(false);
            rvBanner.setNestedScrollingEnabled(false);
            rvBanner.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            rvBanner.setAdapter(adapterDocuments);
            adapterDocuments.doRefresh(bannerListModel.banners);
            adapterDocuments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (!Globals.checkString(bannerListModel.banners.get(position).url).isEmpty()) {
                        if (bannerListModel.banners.get(position).url.startsWith("http://") || bannerListModel.banners.get(position).url.startsWith("https://") || bannerListModel.banners.get(position).url.startsWith("www.")) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(bannerListModel.banners.get(position).url)));
                        } else
                            Toaster.shortToast(bannerListModel.banners.get(position).url);
                    }
                }
            });*/
        } else {
            flBanner.setVisibility(View.GONE);
        }
    }

    /*Test*/

    private void setAdapter() {
        hideNoRecordFound();
        if (adapterHomePropertyDetail == null) {
            if (paginate != null) {
                paginate.unbind();
            }
            adapterHomePropertyDetail = new AdapterHomePropertyDetail(getActivity());
        }
        loading = false;
        adapterHomePropertyDetail.doRefresh(arrPropertyDetailArrayList);

        if (rvHostel.getAdapter() == null) {
            rvHostel.setHasFixedSize(false);
            rvHostel.setNestedScrollingEnabled(false);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), Constant.GRID_SPAN);

            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    switch (adapterHomePropertyDetail.getItemViewType(position)) {
                        case 0:
//                        case 1:
                            return 2;
                        default:
                            return 1;
                    }
                }
            });
            rvHostel.setLayoutManager(gridLayoutManager);
            rvHostel.setItemAnimator(new DefaultItemAnimator());
            rvHostel.setAdapter(adapterHomePropertyDetail);
            if (arrPropertyDetailArrayList.size() < getPropertyDetailModel.total_properties + 1 && rvHostel != null) {
                paginate = Paginate.with(rvHostel, this)
                        .setLoadingTriggerThreshold(Constant.progress_threshold_2)
                        .addLoadingListItem(Constant.addLoadingRow)
                        .setLoadingListItemCreator(new PaginationProgressBarAdapter())
                        .setLoadingListItemSpanSizeLookup(() -> Constant.GRID_SPAN)
                        .build();
            }
        }


        adapterHomePropertyDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getActivity(), HostelDetailActivity.class).putExtra(Constant.Property_id, arrPropertyDetailArrayList.get(position).property_id)
                        .putExtra(Constant.Property_name, arrPropertyDetailArrayList.get(position).property_name));
            }
        });
    }

    @Override
    public void onRefresh() {
        if (Globals.isNetworkAvailable(getActivity())) {
            pageNo = 1;
            getPropertyListData(true, false);
        } else {
            Toaster.shortToast(R.string.no_internet_msg);
        }
    }

    @Override
    public void onLoadMore() {
        loading = true;
        pageNo++;
        getPropertyListData(false, false);
    }

    @Override
    public boolean isLoading() {
        return loading;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return arrPropertyDetailArrayList.size() >= getPropertyDetailModel.total_properties;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (easyWayLocation != null)
            easyWayLocation.beginUpdates();
    }

    @Override
    public void locationOn() {
//        Toaster.shortToast("Location ON");
        easyWayLocation.beginUpdates();

        if (easyWayLocation.getLatitude() != 0 && easyWayLocation.getLongitude() != 0) {
            globals.setLatitude("" + easyWayLocation.getLatitude());
            globals.setLongitude("" + easyWayLocation.getLongitude());
        }
    }

    @Override
    public void onPositionChanged() {
        if (easyWayLocation.getLatitude() != 0 && easyWayLocation.getLongitude() != 0) {
            globals.setLatitude("" + easyWayLocation.getLatitude());
            globals.setLongitude("" + easyWayLocation.getLongitude());
        }

//        Toaster.shortToast(String.valueOf(easyWayLocation.getLongitude()) + "," + String.valueOf(easyWayLocation.getLatitude()));
    }

    @Override
    public void locationCancelled() {
        easyWayLocation.showAlertDialog("Cancelled", "Cancelled Location", null);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (easyWayLocation != null)
            easyWayLocation.endUpdates();
    }


}

