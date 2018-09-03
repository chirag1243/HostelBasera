package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hostelbasera.R;
import com.hostelbasera.apis.HttpRequestHandler;
import com.hostelbasera.apis.PostRequest;
import com.hostelbasera.model.FilterModel;
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

public class CategoryListActivity extends BaseActivity implements Paginate.Callbacks, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.ll_sort)
    LinearLayout llSort;
    @BindView(R.id.ll_filter)
    LinearLayout llFilter;
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

    GetPropertyDetailModel getPropertyDetailModel;
    ArrayList<GetPropertyDetailModel.PropertyDetail> arrPropertyDetailArrayList;

    AdapterCategoryList adapterCategoryList;

    ArrayList<String> arrPropertyCategoryId;
    ArrayList<String> arrPropertyTypeId;
    ArrayList<String> arrTypeId;
    ArrayList<String> arrPropertySizeId;
    FilterModel filterModel;
    private static final int filterCode = 1005;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        ButterKnife.bind(this);
        init();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        globals = ((Globals) this.getApplicationContext());
        progressBar.setVisibility(View.GONE);
        toolbarTitle.setText(getIntent().getStringExtra(Constant.Category_name));

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(this);
        imgBack.setVisibility(View.VISIBLE);

        arrPropertyDetailArrayList = new ArrayList<>();
        tvNoDataFound.setText("");

        arrPropertyTypeId = new ArrayList<>();
        arrTypeId = new ArrayList<>();
        arrPropertySizeId = new ArrayList<>();
        arrPropertyCategoryId = new ArrayList<>();

        arrPropertyCategoryId = getIntent().getStringArrayListExtra(Constant.ArrPropertyCategoryId);

        if (Globals.isNetworkAvailable(this)) {
            getPropertyListData(true);
            getFiltersData();
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
        JSONObject postData = HttpRequestHandler.getInstance().getPropertyListDataParam(pageNo, arrPropertyCategoryId, arrPropertyTypeId, arrTypeId, arrPropertySizeId);

        if (postData != null) {
            if (!swipeRefreshLayout.isRefreshing() && showProgress)
                progressBar.setVisibility(View.VISIBLE);
//            startLoader();

            new PostRequest(this, getString(R.string.getPropertyList),
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
        adapterCategoryList.doRefresh(arrPropertyDetailArrayList);

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
                startActivity(new Intent(CategoryListActivity.this, HostelDetailActivity.class)
                        .putExtra(Constant.Property_id, arrPropertyDetailArrayList.get(position).property_id)
                        .putExtra(Constant.Property_name, arrPropertyDetailArrayList.get(position).property_name));
            }
        });
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

    @OnClick(R.id.ll_sort)
    public void onLlSortClicked() {
        Toaster.shortToast("Coming Soon");
    }

    @OnClick(R.id.ll_filter)
    public void onLlFilterClicked() {
        if (filterModel != null) {
            if (filterModel.status == 0 && filterModel.filterDetail != null) {
                startActivityForResult(new Intent(this, FilterActivity.class).putExtra(Constant.FilterModel, filterModel), filterCode);
            } else {
                Toaster.shortToast(filterModel.message);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                        }
                    }
                }
            }
        }
        getPropertyListData(true);
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
