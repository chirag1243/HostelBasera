package com.hostelbasera.seller;

import android.app.Activity;
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
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hostelbasera.R;
import com.hostelbasera.apis.HttpRequestHandler;
import com.hostelbasera.apis.PostRequest;
import com.hostelbasera.model.CheckSumModel;
import com.hostelbasera.model.GetPropertyDetModel;
import com.hostelbasera.model.SellerPropertyModel;
import com.hostelbasera.model.UserDetailModel;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.Globals;
import com.hostelbasera.utility.PaginationProgressBarAdapter;
import com.hostelbasera.utility.Toaster;
import com.orhanobut.logger.Logger;
import com.paginate.Paginate;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentSellerHome extends Fragment implements Paginate.Callbacks, SwipeRefreshLayout.OnRefreshListener, AdapterSellerHome.OnRenewListener {

    View view;
    Globals globals;
    @BindView(R.id.rv_hostel)
    RecyclerView rvHostel;
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
    int pageNo = 1;
    SellerPropertyModel sellerPropertyModel;
    ArrayList<SellerPropertyModel.PropertyDetail> arrPropertyDetailArrayList;
    AdapterSellerHome adapterSellerHome;
    public int PAYMENT_CODE = 777;

    UserDetailModel.LoginSellerDetail loginSellerDetail;

    public static FragmentSellerHome newInstance() {
        FragmentSellerHome fragment = new FragmentSellerHome();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_seller_home, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {

        Globals.hideKeyboard(getActivity());
        globals = ((Globals) getActivity().getApplicationContext());
        progressBar.setVisibility(View.GONE);
        loginSellerDetail = globals.getUserDetails().loginSellerDetail;

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(this);

        arrPropertyDetailArrayList = new ArrayList<>();
        tvNoDataFound.setText("");


        if (Globals.isNetworkAvailable(getActivity())) {
            getPropertyListData(true);
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
                }
               /* if (dy > 0 && floatingActionButton.getVisibility() == View.VISIBLE) {
                    floatingActionButton.hide();
                } else if (dy < 0 && floatingActionButton.getVisibility() != View.VISIBLE) {
                    floatingActionButton.show();
                }*/
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    public void getPropertyListData(boolean showProgress) {
        JSONObject postData = HttpRequestHandler.getInstance().getSellerPropertyListParam();

        if (postData != null) {
            if (!swipeRefreshLayout.isRefreshing() && showProgress)
                progressBar.setVisibility(View.VISIBLE);
//            startLoader();

            new PostRequest(getActivity(), getString(R.string.getSellerPropertyList), postData, false, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    if (progressBar.getVisibility() == View.VISIBLE)
                        progressBar.setVisibility(View.GONE);
//                    stopLoader();
                    sellerPropertyModel = new Gson().fromJson(response.toString(), SellerPropertyModel.class);
                    if (sellerPropertyModel.propertyDetail != null && !sellerPropertyModel.propertyDetail.isEmpty()) {
                        if (swipeRefreshLayout.isRefreshing()) {
                            try {
                                stopRefreshing();
                                rvHostel.setAdapter(null);
                                arrPropertyDetailArrayList.clear();
                                adapterSellerHome.notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (sellerPropertyModel.propertyDetail != null && !sellerPropertyModel.propertyDetail.isEmpty()) {
                            arrPropertyDetailArrayList.addAll(sellerPropertyModel.propertyDetail);
                            setAdapter();
                        } else
                            showNoRecordFound(getString(R.string.no_data_found));
                    } else {
                        stopRefreshing();
                        if (pageNo == 1) {
                            showNoRecordFound("");
                            Toaster.shortToast(sellerPropertyModel.message);
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

    private void setAdapter() {
        hideNoRecordFound();
        if (adapterSellerHome == null) {
            if (paginate != null) {
                paginate.unbind();
            }
            adapterSellerHome = new AdapterSellerHome(getActivity(), this);
        }
        loading = false;
        adapterSellerHome.doRefresh(arrPropertyDetailArrayList);

        if (rvHostel.getAdapter() == null) {
            rvHostel.setHasFixedSize(false);
            rvHostel.setLayoutManager(new LinearLayoutManager(getActivity()));
            rvHostel.setItemAnimator(new DefaultItemAnimator());
            rvHostel.setAdapter(adapterSellerHome);
            if (arrPropertyDetailArrayList.size() < sellerPropertyModel.total_properties && rvHostel != null) {
                paginate = Paginate.with(rvHostel, this)
                        .setLoadingTriggerThreshold(Constant.progress_threshold_2)
                        .addLoadingListItem(Constant.addLoadingRow)
                        .setLoadingListItemCreator(new PaginationProgressBarAdapter())
                        .setLoadingListItemSpanSizeLookup(() -> Constant.GRID_SPAN)
                        .build();
            }
            rvHostel.getLayoutManager().smoothScrollToPosition(rvHostel, new RecyclerView.State(), rvHostel.getAdapter().getItemCount());
        }

        adapterSellerHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ToDO : Edit Option pending
//                startActivity(new Intent(getActivity(), AddHostelPGActivity.class)
//                        .putExtra(Constant.Property_id, arrPropertyDetailArrayList.get(position).property_id)
//                        .putExtra(Constant.Property_name, arrPropertyDetailArrayList.get(position).property_name));
            }
        });
    }

    private void stopRefreshing() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
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

    @Override
    public void onRefresh() {
        if (Globals.isNetworkAvailable(getActivity())) {
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
        return arrPropertyDetailArrayList.size() >= sellerPropertyModel.total_properties;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    String renewPrice = "";
    int propertyId = 0, sellerId = 0;

    @Override
    public void onRenewToPostCall(String renew_price, int property_id, int seller_id) {
        renewPrice = renew_price;
        propertyId = property_id;
        sellerId = seller_id;
//        startActivityForResult(new Intent(getContext(), PayMentGateWay.class)
//                .putExtra(Constant.Full_name, loginSellerDetail.name)
//                .putExtra(Constant.Phone_number, loginSellerDetail.contact_no)
//                .putExtra(Constant.Email, loginSellerDetail.email)
//                .putExtra(Constant.Price, renew_price), PAYMENT_CODE);
        if (Globals.isNetworkAvailable(getActivity())) {
            doGenerateChecksum("" + renew_price);
        } else {
            Toaster.shortToast(getString(R.string.no_internet_msg));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAYMENT_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                if (data.getStringExtra(Constant.RESULT).equals("Success")) {
                    doRenewProperty(data.getStringExtra(Constant.Payment_id));
                }
            }
        }
    }

    public void doRenewProperty(String payment_id) {
        JSONObject postData = HttpRequestHandler.getInstance().getRenewPropertyParam(propertyId,sellerId ,payment_id , renewPrice);

        if (postData != null) {
            new PostRequest(getContext(), getString(R.string.renewProperty), postData, true,
                    new PostRequest.OnPostServiceCallListener() {
                        @Override
                        public void onSucceedToPostCall(JSONObject response) {
                            GetPropertyDetModel getPropertyDetModel = new Gson().fromJson(response.toString(), GetPropertyDetModel.class);
                            if (getPropertyDetModel.status == 0) {
                                if (Globals.isNetworkAvailable(getActivity())) {
                                    getPropertyListData(true);
                                } else {
                                    showNoRecordFound(getString(R.string.no_data_found));
                                    Toaster.shortToast(R.string.no_internet_msg);
                                }
                            } else
                                Toaster.shortToast(getPropertyDetModel.message);
                        }

                        @Override
                        public void onFailedToPostCall(int statusCode, String msg) {
                            Toaster.shortToast(msg);
                        }
                    }).execute();
        }
    }

    String OrderId = "", CustId = "";

    private void doGenerateChecksum(String amount) {
        OrderId = "Order" + Globals.randomNumber();
        CustId = "Cust" + Globals.randomNumber();
        JSONObject postData = HttpRequestHandler.getInstance().doGenerateChecksumParam(OrderId, CustId, amount);
        if (postData != null) {

            new PostRequest(getActivity(), getString(R.string.main_url), getString(R.string.generateChecksum), postData, true, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    CheckSumModel checkSumModel = new Gson().fromJson(response.toString(), CheckSumModel.class);
                    if (Globals.isNetworkAvailable(getActivity())) {
                        onStartTransaction(checkSumModel.CHECKSUMHASH, amount);
                    } else {
                        Toaster.shortToast(getString(R.string.no_internet_msg));
                    }

                }

                @Override
                public void onFailedToPostCall(int statusCode, String msg) {
                    Toaster.shortToast(msg);
                }
            }).execute();
        }
        Globals.hideKeyboard(getActivity());
    }


    public void onStartTransaction(String CHECKSUMHASH, String Amount) {
        PaytmPGService Service = PaytmPGService.getProductionService();

        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put(Constant.MID, Constant.MID_Value);
        paramMap.put(Constant.ORDER_ID, OrderId);
        paramMap.put(Constant.CUST_ID, CustId);
        paramMap.put(Constant.INDUSTRY_TYPE_ID, Constant.INDUSTRY_TYPE_ID_Value);
        paramMap.put(Constant.CHANNEL_ID, Constant.CHANNEL_ID_Value);
        paramMap.put(Constant.TXN_AMOUNT, Amount);
        paramMap.put(Constant.WEBSITE, Constant.WEBSITE_Value);
        paramMap.put(Constant.CALLBACK_URL, Constant.CALLBACK_URL_Value + OrderId);
        paramMap.put(Constant.CHECKSUMHASH, CHECKSUMHASH);

        PaytmOrder Order = new PaytmOrder(paramMap);

        Service.initialize(Order, null);

        Service.startPaymentTransaction(getActivity(), true, true,
                new PaytmPaymentTransactionCallback() {
                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        Toaster.longToast(inErrorMessage);
                        // Some UI Error Occurred in Payment Gateway Activity.
                        // // This may be due to initialization of views in
                        // Payment Gateway Activity or may be due to //
                        // initialization of webview. // Error Message details
                        // the error occurred.
                    }


                    @Override
                    public void onTransactionResponse(Bundle inResponse) {
                        Logger.e("LOG", "Payment Transaction is  " + inResponse);

                        if (inResponse.containsKey("STATUS") && inResponse.getString("STATUS").equals("TXN_SUCCESS")) {
                            Toaster.shortToast("Payment Transaction is successful");
                            doRenewProperty(inResponse.containsKey("BANKTXNID") ? inResponse.getString("BANKTXNID") : "");
                        } else {
                            Toaster.longToast("Payment Transaction response (" + (inResponse.containsKey("RESPMSG") ? inResponse.getString("RESPMSG") : "Failure") + ")");
                        }
                    }

                    @Override
                    public void networkNotAvailable() { // If network is not
                        Toaster.longToast(getString(R.string.no_internet_msg));
                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        Toaster.longToast(inErrorMessage);
                        // This method gets called if client authentication
                        // failed. // Failure may be due to following reasons //
                        // 1. Server error or downtime. // 2. Server unable to
                        // generate checksum or checksum response is not in
                        // proper format. // 3. Server failed to authenticate
                        // that client. That is value of payt_STATUS is 2. //
                        // Error Message describes the reason for failure.

                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                        Toaster.longToast(inErrorMessage);
                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        Toaster.longToast("Back pressed. Transaction cancelled");
                    }

                    @Override
                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                        Toaster.longToast("Payment Transaction Failed " + inErrorMessage);
                        Logger.e("LOG", "Payment Transaction Failed " + inErrorMessage);
                    }

                });
    }
}


