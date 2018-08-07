package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hostelbasera.R;
import com.hostelbasera.apis.HttpRequestHandler;
import com.hostelbasera.apis.PostRequest;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.Globals;
import com.hostelbasera.utility.Toaster;
import com.paginate.Paginate;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FragmentHome extends Fragment /*implements Paginate.Callbacks, SwipeRefreshLayout.OnRefreshListener */{

    Globals globals;
    View view;
    /*@BindView(R.id.search_view)
    TextView searchView;
    @BindView(R.id.rv_category)
    RecyclerView rvCategory;
    @BindView(R.id.ll_category)
    LinearLayout llCategory;
    //    @BindView(R.id.vp_banner)
//    ViewPager vpBanner;
    @BindView(R.id.rv_StoreDetail)
    RecyclerView rvStoreDetail;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tv_no_data_found)
    TextView tvNoDataFound;
    @BindView(R.id.rotate_loading)
    RotateLoading rotateLoading;


    private Paginate paginate;
    private boolean loading = false;

    DashboardPagerAdapter mDashboardPagerAdapter;
    int currentPage = 0;
    int pageNo = 1;
    Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.

    AllCategoriesDetailModel allCategoriesModel;
    AllHomePageStoresDetailModel homePageStoresDetailModel;

    ArrayList<AllHomePageStoresDetailModel.HomePageStoresDetail> arrHomePageStoresDetailArrayList;
    AdapterHomeStoresDetail adapterHomeStoresDetail;
    DashboardActivity activity;*/

    /*public static FragmentHome newInstance(AllCategoriesDetailModel model) {
        FragmentHome fragment = new FragmentHome();
        fragment.allCategoriesModel = model;
        return fragment;
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
//        init();
        return view;
    }

   /* @SuppressLint("SetTextI18n")
    private void init() {
        Globals.hideKeyboard(getActivity());
        globals = ((Globals) getActivity().getApplicationContext());
        activity = (DashboardActivity) getActivity();
        progressBar.setVisibility(View.GONE);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(this);

//        searchView.setQuery("", false);
//        searchView.setIconified(false);
//        searchView.clearFocus();
        if (allCategoriesModel != null && allCategoriesModel.all_categories_detail != null) {
            llCategory.setVisibility(View.VISIBLE);
            setUpCategory();
        } else {
            llCategory.setVisibility(View.GONE);
        }
        arrHomePageStoresDetailArrayList = new ArrayList<>();
        tvNoDataFound.setText("");

        if (Globals.isNetworkAvailable(getActivity())) {
//            vpBanner.setVisibility(View.GONE);
//            getSliderImages();
            getHomePageStoresDetail(true);
        } else {
            showNoRecordFound(getString(R.string.no_data_found));
            Toaster.shortToast(R.string.no_internet_msg);
        }

    }

    @OnClick(R.id.search_view)
    public void onSearchClick() {
        startActivity(new Intent(getActivity(), SearchActivity.class));
    }


    private void setUpCategory() {
        rvCategory.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvCategory.setItemAnimator(new DefaultItemAnimator());
//        ArrayList<AllCategoriesDetailModel.All_categories_detail> arrCategory = new ArrayList<>();
//
//        for (int i = 0; i < allCategoriesModel.all_categories_detail.size(); i++) {
//            arrCategory.add(allCategoriesModel.all_categories_detail.get(i));
////            if (i == 5) {
////                break;
////            }
//        }
        rvCategory.setAdapter(new HomeCategoryAdapter(getActivity(), allCategoriesModel.all_categories_detail));
    }

    private void showNoRecordFound(String no_data_found) {
        loading = false;
        rvStoreDetail.setVisibility(View.GONE);
        if (tvNoDataFound.getVisibility() == View.GONE) {
            tvNoDataFound.setVisibility(View.VISIBLE);
            tvNoDataFound.setText(no_data_found);
        }
    }

    private void hideNoRecordFound() {
        rvStoreDetail.setVisibility(View.VISIBLE);
        if (tvNoDataFound.getVisibility() == View.VISIBLE)
            tvNoDataFound.setVisibility(View.GONE);
    }

    *//*public void stopLoader() {
        rotateLoading.stop();
        rotateLoading.setVisibility(View.GONE);
        flRotateLoading.setVisibility(View.GONE);
    }*//*

    *//*public void startLoader() {
        flRotateLoading.setVisibility(View.VISIBLE);
        rotateLoading.setVisibility(View.VISIBLE);
        rotateLoading.start();
        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(10000);
        rotate.setInterpolator(new LinearInterpolator());

        rotate.setRepeatCount(Animation.INFINITE);
        imgIcon.startAnimation(rotate);
    }*//*

    public void getHomePageStoresDetail(boolean showProgress) {
        JSONObject postData = HttpRequestHandler.getInstance().getHomePageStoresDetailParam(pageNo);

        if (postData != null) {
            if (!swipeRefreshLayout.isRefreshing() && showProgress)
                progressBar.setVisibility(View.VISIBLE);
//            startLoader();

            new PostRequest(getActivity(), getString(R.string.getHomePageStoresDetail),
                    postData, false, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    if (progressBar.getVisibility() == View.VISIBLE)
                        progressBar.setVisibility(View.GONE);
//                    stopLoader();

                    homePageStoresDetailModel = new Gson().fromJson(response.toString(), AllHomePageStoresDetailModel.class);
                    if (homePageStoresDetailModel.homePageStoresDetail != null && !homePageStoresDetailModel.homePageStoresDetail.isEmpty()) {
                        if (swipeRefreshLayout.isRefreshing()) {
                            stopRefreshing();
                            rvStoreDetail.setAdapter(null);
                            arrHomePageStoresDetailArrayList.clear();
                            adapterHomeStoresDetail.notifyDataSetChanged();
                        }
                        setupList(homePageStoresDetailModel.homePageStoresDetail);
                    } else {
                        stopRefreshing();
                        if (homePageStoresDetailModel.homePageStoresDetail != null && homePageStoresDetailModel.doNextCall && !homePageStoresDetailModel.isPageEnded) {
                            pageNo++;
                            getHomePageStoresDetail(false);
                        } else {
                            if (homePageStoresDetailModel.homePageStoresDetail != null && homePageStoresDetailModel.total_stores == 0) {
                                showNoRecordFound("");
                            } else {
                                if (paginate != null)
                                    paginate.unbind();
                            }
                            if (pageNo == 1)
                                Toaster.shortToast(homePageStoresDetailModel.message);
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

    private void setupList(ArrayList<AllHomePageStoresDetailModel.HomePageStoresDetail> homePageStoresDetailArrayList) {
        if (homePageStoresDetailArrayList != null && !homePageStoresDetailArrayList.isEmpty()) {
            arrHomePageStoresDetailArrayList.addAll(homePageStoresDetailArrayList);
            setAdapter();
        } else
            showNoRecordFound(getString(R.string.no_data_found));
    }

    private void setAdapter() {
        hideNoRecordFound();
        if (adapterHomeStoresDetail == null) {
            if (paginate != null) {
                paginate.unbind();
            }
            adapterHomeStoresDetail = new AdapterHomeStoresDetail(getActivity(), activity);
        }
        loading = false;
        adapterHomeStoresDetail.doRefresh(arrHomePageStoresDetailArrayList);

        if (rvStoreDetail.getAdapter() == null) {
            rvStoreDetail.setLayoutManager(new LinearLayoutManager(getActivity()));
            rvStoreDetail.setItemAnimator(new DefaultItemAnimator());
            rvStoreDetail.setAdapter(adapterHomeStoresDetail);
            if (!homePageStoresDetailModel.isPageEnded && rvStoreDetail != null) {
                paginate = Paginate.with(rvStoreDetail, this)
                        .setLoadingTriggerThreshold(Constant.progress_threshold_2)
                        .addLoadingListItem(Constant.addLoadingRow)
                        .setLoadingListItemCreator(new paginationProgressBarAdapter())
                        .setLoadingListItemSpanSizeLookup(() -> Constant.GRID_SPAN)
                        .build();
            }
        }
    }

    @Override
    public void onRefresh() {
        if (Globals.isNetworkAvailable(getActivity())) {
//            getSliderImages();
            pageNo = 1;
            getHomePageStoresDetail(true);
        } else {
            Toaster.shortToast(R.string.no_internet_msg);
        }
    }

    @Override
    public void onLoadMore() {
        loading = true;
        pageNo++;
        getHomePageStoresDetail(false);
    }

    @Override
    public boolean isLoading() {
        return loading;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return homePageStoresDetailModel.isPageEnded; //arrHomePageStoresDetailArrayList.size() == homePageStoresDetailModel.total_stores
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapterHomeStoresDetail != null) {
            if (adapterHomeStoresDetail.itemListDataAdapter != null) {
                adapterHomeStoresDetail.itemListDataAdapter.size_Id = 0;
            }
        }
    }*/


}

