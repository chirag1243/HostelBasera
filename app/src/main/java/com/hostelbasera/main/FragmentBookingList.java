package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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
import com.hostelbasera.model.BookingListDataModel;
import com.hostelbasera.model.OrderDetailModel;
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

/**
 * Created by Chirag on 29/10/20.
 */
public class FragmentBookingList extends Fragment implements Paginate.Callbacks, SwipeRefreshLayout.OnRefreshListener, AdapterBookingList.OnBookingStatusListener {

    View view;
    @BindView(R.id.rv_booking_list)
    RecyclerView rvBookingList;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    //    @BindView(R.id.rotate_loading)
//    RotateLoading rotateLoading;
    @BindView(R.id.img_icon)
    ImageView imgIcon;
    @BindView(R.id.fl_rotate_loading)
    FrameLayout flRotateLoading;
    @BindView(R.id.tv_no_data_found)
    TextView tvNoDataFound;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    Globals globals;
    BookingListDataModel bookingListDataModel;
    ArrayList<BookingListDataModel.Booking_requests> arrBookingList;
    AdapterBookingList adapterBookingList;
    Activity mActivity;

    int pageNo = 1;
    private Paginate paginate;
    private boolean loading = false, isSeller;

    public static FragmentBookingList newInstance(boolean isSeller) {
        FragmentBookingList fragment = new FragmentBookingList();
        fragment.isSeller = isSeller;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_booking_list, container, false);
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
        arrBookingList = new ArrayList<>();

        if (Globals.isNetworkAvailable(mActivity)) {
            getBookingListData(true);
        } else {
            Toaster.shortToast(R.string.no_internet_msg);
        }

        rvBookingList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (rvBookingList.getChildAt(0) != null) {
                    swipeRefreshLayout.setEnabled(rvBookingList.getChildAt(0).getTop() == 0);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }


    public void getBookingListData(boolean showProgress) {
        JSONObject postData = HttpRequestHandler.getInstance().getBookingListDataParam(pageNo, isSeller);

        if (postData != null) {
            if (!swipeRefreshLayout.isRefreshing() && showProgress)
                progressBar.setVisibility(View.VISIBLE);

            new PostRequest(mActivity, getString(R.string.bookingRequestList),
                    postData, showProgress, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    if (progressBar.getVisibility() == View.VISIBLE)
                        progressBar.setVisibility(View.GONE);

                    bookingListDataModel = new Gson().fromJson(response.toString(), BookingListDataModel.class);
                    if (bookingListDataModel.status == 0 && bookingListDataModel.booking_requests != null && !bookingListDataModel.booking_requests.isEmpty()) {
                        if (swipeRefreshLayout.isRefreshing()) {
                            try {
                                stopRefreshing();
                                rvBookingList.setAdapter(null);
                                arrBookingList.clear();
                                adapterBookingList.notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        setupList(bookingListDataModel.booking_requests);
                    } else {
                        stopRefreshing();
                        if (pageNo == 1) {
                            showNoRecordFound("");
                            Toaster.shortToast(bookingListDataModel.message);
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
        rvBookingList.setVisibility(View.GONE);
        if (tvNoDataFound.getVisibility() == View.GONE) {
            tvNoDataFound.setVisibility(View.VISIBLE);
            tvNoDataFound.setText(no_data_found);
        }
    }

    private void hideNoRecordFound() {
        rvBookingList.setVisibility(View.VISIBLE);
        if (tvNoDataFound.getVisibility() == View.VISIBLE)
            tvNoDataFound.setVisibility(View.GONE);
    }

    private void stopRefreshing() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void setupList(ArrayList<BookingListDataModel.Booking_requests> arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            arrBookingList.addAll(arrayList);
//            setAllMarkers();
            setAdapter();
        } else
            showNoRecordFound(getString(R.string.no_data_found));
    }


    private void setAdapter() {
        if (adapterBookingList == null) {
            adapterBookingList = new AdapterBookingList(getActivity());
        }
        adapterBookingList.doRefresh(arrBookingList, isSeller, this);

        if (rvBookingList.getAdapter() == null) {
            rvBookingList.setHasFixedSize(false);
            rvBookingList.setLayoutManager(new LinearLayoutManager(getActivity()));
            rvBookingList.setItemAnimator(new DefaultItemAnimator());
            rvBookingList.setAdapter(adapterBookingList);
            if (arrBookingList.size() < bookingListDataModel.total_booking_requests && rvBookingList != null) {
                paginate = Paginate.with(rvBookingList, this)
                        .setLoadingTriggerThreshold(Constant.progress_threshold_2)
                        .addLoadingListItem(Constant.addLoadingRow)
                        .setLoadingListItemCreator(new PaginationProgressBarAdapter())
                        .setLoadingListItemSpanSizeLookup(() -> Constant.GRID_SPAN)
                        .build();
            }
        }

        adapterBookingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        getBookingListData(false);
    }

    @Override
    public boolean isLoading() {
        return loading;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return arrBookingList.size() >= bookingListDataModel.total_booking_requests;
    }

    @Override
    public void onResume() {
        super.onResume();
//        arrBookingList = new ArrayList<>();
//        if (adapterBookingList != null)
//            setAdapter();
//        pageNo = 1;
//        onRefresh();
    }

    @Override
    public void onRefresh() {
        if (Globals.isNetworkAvailable(mActivity)) {
            getBookingListData(true);
        } else {
            Toaster.shortToast(R.string.no_internet_msg);
        }
    }

    @Override
    public void onChangeStatus(int id, int status) {
        if (Globals.isNetworkAvailable(mActivity)) {

            JSONObject postData = HttpRequestHandler.getInstance().getChangeBookingStatusDataParam(id, status);

            if (postData != null) {
                new PostRequest(mActivity, getString(R.string.changeBookingStatus), postData, true, new PostRequest.OnPostServiceCallListener() {
                    @Override
                    public void onSucceedToPostCall(JSONObject response) {
                        OrderDetailModel orderDetailModel = new Gson().fromJson(response.toString(), OrderDetailModel.class);
                        if (orderDetailModel.status == 0) {
                            arrBookingList = new ArrayList<>();
                            setAdapter();
                            pageNo = 1;
                            onRefresh();
                        }
//                        else {
                        Toaster.shortToast(orderDetailModel.message);
//                        }
                    }

                    @Override
                    public void onFailedToPostCall(int statusCode, String msg) {
                        Toaster.shortToast(msg);
                    }
                }).execute();
            }
            Globals.hideKeyboard(getActivity());

        } else {
            Toaster.shortToast(R.string.no_internet_msg);
        }
    }


    @Override
    public void onMakePayment(int id, int seller_id, int property_id, int price) {
        startActivity(new Intent(getActivity(), BookingPaymentActivity.class)
                .putExtra(Constant.Property_price, price)
                .putExtra(Constant.Seller_id, seller_id)
                .putExtra(Constant.Property_id, property_id)
                .putExtra(Constant.User_property_request_id, id));

        // .putExtra(Constant.Property_name, arrBookingList.get(position).property_name)
    }

}

