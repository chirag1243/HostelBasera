package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.hostelbasera.model.OrderDetailModel;
import com.hostelbasera.utility.BaseActivity;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.Globals;
import com.hostelbasera.utility.Toaster;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.rv_order_list)
    RecyclerView rvOrderList;
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

    private boolean loading = false;

    Globals globals;
    ArrayList<OrderDetailModel.OrderDetails> arOrderDetailsArrayList;
    AdapterOrderList adapterOrderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        ButterKnife.bind(this);
        init();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        globals = ((Globals) this.getApplicationContext());
        progressBar.setVisibility(View.GONE);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(this);
        imgBack.setVisibility(View.VISIBLE);

        arOrderDetailsArrayList = new ArrayList<>();
        tvNoDataFound.setText("");
        if (Globals.isNetworkAvailable(this)) {
            getOrderListData(true);
        } else {
            showNoRecordFound(getString(R.string.no_data_found));
            Toaster.shortToast(R.string.no_internet_msg);
        }

        rvOrderList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (rvOrderList.getChildAt(0) != null) {
                    swipeRefreshLayout.setEnabled(rvOrderList.getChildAt(0).getTop() == 0);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    public void getOrderListData(boolean showProgress) {
        JSONObject postData = HttpRequestHandler.getInstance().getOrderDataParam();

        if (postData != null) {
            if (!swipeRefreshLayout.isRefreshing() && showProgress)
                progressBar.setVisibility(View.VISIBLE);

            new PostRequest(this, getString(R.string.getOrderData), postData, false, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    if (progressBar.getVisibility() == View.VISIBLE)
                        progressBar.setVisibility(View.GONE);

                    OrderDetailModel orderDetailModel = new Gson().fromJson(response.toString(), OrderDetailModel.class);
                    if (orderDetailModel.status == 0 && orderDetailModel.OrderDetails != null) {
                        arOrderDetailsArrayList = orderDetailModel.OrderDetails;
                        if (swipeRefreshLayout.isRefreshing()) {
                            stopRefreshing();
                            rvOrderList.setAdapter(null);
                            arOrderDetailsArrayList.clear();
                            adapterOrderList.notifyDataSetChanged();
                        }
                        setupList(orderDetailModel.OrderDetails);
                    } else {
                        stopRefreshing();
                        showNoRecordFound("");
                        Toaster.shortToast(orderDetailModel.message);
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

    private void showNoRecordFound(String no_data_found) {
        loading = false;
        rvOrderList.setVisibility(View.GONE);
        if (tvNoDataFound.getVisibility() == View.GONE) {
            tvNoDataFound.setVisibility(View.VISIBLE);
            tvNoDataFound.setText(no_data_found);
        }
    }

    private void hideNoRecordFound() {
        rvOrderList.setVisibility(View.VISIBLE);
        if (tvNoDataFound.getVisibility() == View.VISIBLE)
            tvNoDataFound.setVisibility(View.GONE);
    }

    private void stopRefreshing() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void setupList(ArrayList<OrderDetailModel.OrderDetails> orderDetailsArrayList) {
        if (orderDetailsArrayList != null && !orderDetailsArrayList.isEmpty()) {
            arOrderDetailsArrayList.addAll(orderDetailsArrayList);
            setAdapter();
        } else
            showNoRecordFound(getString(R.string.no_data_found));
    }

    private void setAdapter() {
        hideNoRecordFound();
        if (adapterOrderList == null) {
            adapterOrderList = new AdapterOrderList(this);
        }
        loading = false;
        adapterOrderList.doRefresh(arOrderDetailsArrayList);

        if (rvOrderList.getAdapter() == null) {
            rvOrderList.setHasFixedSize(false);
            rvOrderList.setLayoutManager(new LinearLayoutManager(this));
            rvOrderList.setItemAnimator(new DefaultItemAnimator());
            rvOrderList.setAdapter(adapterOrderList);

        }

        adapterOrderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(OrderListActivity.this, HostelDetailActivity.class)
                        .putExtra(Constant.Property_id, arOrderDetailsArrayList.get(position).property_id)
                        .putExtra(Constant.Property_name, arOrderDetailsArrayList.get(position).property_name));
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
            getOrderListData(true);
        } else {
            Toaster.shortToast(R.string.no_internet_msg);
        }
    }
}
