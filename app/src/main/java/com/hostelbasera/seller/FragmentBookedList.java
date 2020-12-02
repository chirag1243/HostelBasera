package com.hostelbasera.seller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hostelbasera.R;
import com.hostelbasera.apis.HttpRequestHandler;
import com.hostelbasera.apis.PostRequest;
import com.hostelbasera.model.OrderListModel;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.Globals;
import com.hostelbasera.utility.PaginationProgressBarAdapter;
import com.hostelbasera.utility.Toaster;
import com.paginate.Paginate;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentBookedList extends Fragment implements Paginate.Callbacks, SwipeRefreshLayout.OnRefreshListener{

    View view;
    @BindView(R.id.rv_order_list)
    RecyclerView rvOrderList;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.img_icon)
    ImageView imgIcon;
    @BindView(R.id.fl_rotate_loading)
    FrameLayout flRotateLoading;
    @BindView(R.id.tv_no_data_found)
    TextView tvNoDataFound;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    Globals globals;

    OrderListModel orderListModel;
    ArrayList<OrderListModel.Order_list> arrOrderList;
    AdapterOrderList adapterOrderList;

    int pageNo = 1;
    private Paginate paginate;
    private boolean loading = false, isSeller;
    Activity mActivity;


    public static FragmentBookedList newInstance(boolean isSeller) {
        FragmentBookedList fragment = new FragmentBookedList();
        fragment.isSeller = isSeller;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_list, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        mActivity = getActivity();
        globals = ((Globals) Objects.requireNonNull(mActivity).getApplicationContext());

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(this);
        tvNoDataFound.setText("");
        arrOrderList = new ArrayList<>();

        if (Globals.isNetworkAvailable(getActivity())) {
            getOrderListData(true);
        } else {
            Toaster.shortToast(R.string.no_internet_msg);
        }

        rvOrderList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (rvOrderList.getChildAt(0) != null) {
                    swipeRefreshLayout.setEnabled(rvOrderList.getChildAt(0).getTop() == 0);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }


    public void getOrderListData(boolean showProgress) {
        JSONObject postData = HttpRequestHandler.getInstance().getOrderListDataParam(pageNo, isSeller);

        if (postData != null) {
            if (!swipeRefreshLayout.isRefreshing() && showProgress)
                progressBar.setVisibility(View.VISIBLE);

            new PostRequest(mActivity, getString(R.string.orderList),
                    postData, showProgress, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    if (progressBar.getVisibility() == View.VISIBLE)
                        progressBar.setVisibility(View.GONE);

                    orderListModel = new Gson().fromJson(response.toString(), OrderListModel.class);
                    if (orderListModel.status == 0 && orderListModel.order_list != null && !orderListModel.order_list.isEmpty()) {
                        if (swipeRefreshLayout.isRefreshing()) {
                            try {
                                stopRefreshing();
                                rvOrderList.setAdapter(null);
                                arrOrderList.clear();
                                adapterOrderList.notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        setupList(orderListModel.order_list);
                    } else {
                        stopRefreshing();
                        if (pageNo == 1) {
                            showNoRecordFound("");
                            Toaster.shortToast(orderListModel.message);
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

    private void setupList(ArrayList<OrderListModel.Order_list> arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            arrOrderList.addAll(arrayList);
//            setAllMarkers();
            setAdapter();
        } else
            showNoRecordFound(getString(R.string.no_data_found));
    }


    private void setAdapter() {
        if (adapterOrderList == null) {
            adapterOrderList = new AdapterOrderList(getActivity());
        }
        adapterOrderList.doRefresh(arrOrderList, isSeller);

        if (rvOrderList.getAdapter() == null) {
            rvOrderList.setHasFixedSize(false);
            rvOrderList.setLayoutManager(new LinearLayoutManager(getActivity()));
            rvOrderList.setItemAnimator(new DefaultItemAnimator());
            rvOrderList.setAdapter(adapterOrderList);
            if (arrOrderList.size() < orderListModel.total_orders && rvOrderList != null) {
                paginate = Paginate.with(rvOrderList, this)
                        .setLoadingTriggerThreshold(Constant.progress_threshold_2)
                        .addLoadingListItem(Constant.addLoadingRow)
                        .setLoadingListItemCreator(new PaginationProgressBarAdapter())
                        .setLoadingListItemSpanSizeLookup(() -> Constant.GRID_SPAN)
                        .build();
            }
        }

        adapterOrderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                startActivity(new Intent(getActivity(), HostelDetailActivity.class)
//                        .putExtra(Constant.Property_id, arrBookingList.get(position).property_id)
//                        .putExtra(Constant.Property_name, arrBookingList.get(position).property_name));
            }
        });
    }

    @Override
    public void onLoadMore() {
        loading = true;
        pageNo++;
        getOrderListData(false);
    }

    @Override
    public boolean isLoading() {
        return loading;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return arrOrderList.size() >= orderListModel.total_orders;
    }


    @Override
    public void onRefresh() {
        if (Globals.isNetworkAvailable(mActivity)) {
            getOrderListData(true);
        } else {
            Toaster.shortToast(R.string.no_internet_msg);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
