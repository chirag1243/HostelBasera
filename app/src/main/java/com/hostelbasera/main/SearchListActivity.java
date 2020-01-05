package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hostelbasera.R;
import com.hostelbasera.apis.HttpRequestHandler;
import com.hostelbasera.apis.PostRequest;
import com.hostelbasera.model.GetPropertyDetailModel;
import com.hostelbasera.utility.BaseActivity;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.Globals;
import com.hostelbasera.utility.PaginationProgressBarAdapter;
import com.hostelbasera.utility.Toaster;
import com.paginate.Paginate;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchListActivity extends BaseActivity implements Paginate.Callbacks, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.rv_hostel_list)
    RecyclerView rvHostelList;
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
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private Paginate paginate;
    private boolean loading = false;
    Globals globals;

    int pageNo = 1;
    String searchText = "";

    GetPropertyDetailModel getPropertyDetailModel;
    ArrayList<GetPropertyDetailModel.PropertyDetail> arrPropertyDetailArrayList;

    AdapterCategoryList adapterCategoryList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);
        ButterKnife.bind(this);
        init();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        globals = ((Globals) this.getApplicationContext());
        progressBar.setVisibility(View.GONE);
        searchText = getIntent().getStringExtra(Constant.Searchtext);
        toolbarTitle.setText(searchText);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(this);
        imgBack.setVisibility(View.VISIBLE);

        arrPropertyDetailArrayList = new ArrayList<>();
        tvNoDataFound.setText("");

        if (Globals.isNetworkAvailable(this)) {
            getPropertyListData(true);
        } else {
            showNoRecordFound(getString(R.string.no_data_found));
            Toaster.shortToast(R.string.no_internet_msg);
        }

        rvHostelList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (rvHostelList.getChildAt(0) != null) {
                    swipeRefreshLayout.setEnabled(rvHostelList.getChildAt(0).getTop() == 0);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    public void getPropertyListData(boolean showProgress) {
        JSONObject postData = HttpRequestHandler.getInstance().getSearchListDataParam(pageNo, searchText);

        if (postData != null) {
            if (!swipeRefreshLayout.isRefreshing() && showProgress)
                progressBar.setVisibility(View.VISIBLE);
//            startLoader();

            new PostRequest(this, getString(R.string.searchPropertyList),
                    postData, false, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    if (progressBar.getVisibility() == View.VISIBLE)
                        progressBar.setVisibility(View.GONE);
//                    stopLoader();
                    getPropertyDetailModel = new Gson().fromJson(response.toString(), GetPropertyDetailModel.class);
                    if (getPropertyDetailModel.propertyDetail != null && !getPropertyDetailModel.propertyDetail.isEmpty()) {
                        if (swipeRefreshLayout.isRefreshing()) {
                            stopRefreshing();
                            rvHostelList.setAdapter(null);
                            arrPropertyDetailArrayList.clear();
                            adapterCategoryList.notifyDataSetChanged();
                        }
                        setupList(getPropertyDetailModel.propertyDetail);
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
        Globals.hideKeyboard(this);
    }

    private void showNoRecordFound(String no_data_found) {
        loading = false;
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

    private void stopRefreshing() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void setupList(ArrayList<GetPropertyDetailModel.PropertyDetail> homePageStoresDetailArrayList) {
        if (homePageStoresDetailArrayList != null && !homePageStoresDetailArrayList.isEmpty()) {
            arrPropertyDetailArrayList.addAll(homePageStoresDetailArrayList);
            setAdapter();
        } else
            showNoRecordFound(getString(R.string.no_data_found));
    }

    private void setAdapter() {
        hideNoRecordFound();
        if (adapterCategoryList == null) {
            if (paginate != null) {
                paginate.unbind();
            }
            adapterCategoryList = new AdapterCategoryList(this);
        }
        loading = false;
        adapterCategoryList.doRefresh(arrPropertyDetailArrayList, false,true);

        if (rvHostelList.getAdapter() == null) {
            rvHostelList.setHasFixedSize(false);
            rvHostelList.setLayoutManager(new GridLayoutManager(this, Constant.GRID_SPAN));
            rvHostelList.setItemAnimator(new DefaultItemAnimator());
            rvHostelList.setAdapter(adapterCategoryList);
            if (arrPropertyDetailArrayList.size() < getPropertyDetailModel.total_properties && rvHostelList != null) {
                paginate = Paginate.with(rvHostelList, this)
                        .setLoadingTriggerThreshold(Constant.progress_threshold_2)
                        .addLoadingListItem(Constant.addLoadingRow)
                        .setLoadingListItemCreator(new PaginationProgressBarAdapter())
                        .setLoadingListItemSpanSizeLookup(() -> Constant.GRID_SPAN)
                        .build();
            }
        }

        adapterCategoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(SearchListActivity.this, HostelDetailActivity.class)
                        .putExtra(Constant.Property_id, arrPropertyDetailArrayList.get(position).property_id)
                        .putExtra(Constant.Property_name, arrPropertyDetailArrayList.get(position).property_name));
            }
        });
    }

    @OnClick(R.id.img_back)
    public void onViewClicked() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public void onRefresh() {
        if (Globals.isNetworkAvailable(this)) {
            pageNo = 1;
            getPropertyListData(true);
        } else {
            Toaster.shortToast(R.string.no_internet_msg);
        }
    }

    @Override
    public void onLoadMore() {
        loading = true;
        pageNo++;
        getPropertyListData(false);
    }

    @Override
    public boolean isLoading() {
        return loading;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return arrPropertyDetailArrayList.size() >= getPropertyDetailModel.total_properties;
    }
}
