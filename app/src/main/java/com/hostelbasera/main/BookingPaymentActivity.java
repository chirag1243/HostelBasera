package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hostelbasera.R;
import com.hostelbasera.apis.HttpRequestHandler;
import com.hostelbasera.apis.PostRequest;
import com.hostelbasera.model.CheckSumModel;
import com.hostelbasera.model.CouponsModel;
import com.hostelbasera.model.UserWalletBalanceModel;
import com.hostelbasera.utility.BaseActivity;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.Globals;
import com.hostelbasera.utility.Toaster;
import com.orhanobut.logger.Logger;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BookingPaymentActivity extends BaseActivity implements AdapterCouponList.OnCouponApplyListener {

    @BindView(R.id.tv_lbl_wallet_balance)
    TextView tvLblWalletBalance;
    @BindView(R.id.chk_balance)
    CheckBox chkBalance;
    @BindView(R.id.rv_coupon)
    RecyclerView rvCoupon;
    @BindView(R.id.tv_lbl_amount_details)
    TextView tvLblAmountDetails;
    @BindView(R.id.tv_rent)
    TextView tvRent;
    @BindView(R.id.tv_coupon)
    TextView tvCoupon;
    @BindView(R.id.tv_wallet)
    TextView tvWallet;
    @BindView(R.id.tv_amount_payable)
    TextView tvAmountPayable;
    @BindView(R.id.tv_total)
    TextView tvTotal;
    @BindView(R.id.btn_payment)
    Button btnPayment;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tv_no_data_found)
    TextView tvNoDataFound;

    @BindView(R.id.ll_coupon)
    LinearLayout llCoupon;
    @BindView(R.id.ll_wallet)
    LinearLayout llWallet;

    @BindView(R.id.ll_coupon_applied)
    LinearLayout llCouponApplied;
    @BindView(R.id.tv_coupon_applied)
    TextView tvCouponApplied;
    @BindView(R.id.tv_remove)
    TextView tvRemove;

    @BindView(R.id.vw_coupon)
    View vwCoupon;
    @BindView(R.id.vw_wallet)
    View vwWallet;


    int wallet_balance = 0, user_property_request_id = 0,
            property_price = 0, coupon_price = 0, main_Wallet = 0, total = 0, seller_id, property_id, coupon_id = 0, coupon_amount_type = 0;
    ArrayList<CouponsModel.Coupons> arrCoupons;
    AdapterCouponList adapterCouponList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_payment);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        Globals.doBoldTextView(tvLblWalletBalance);
        Globals.doBoldTextView(tvLblAmountDetails);
        Globals.doBoldTextView(tvRent);
        Globals.doBoldTextView(tvCoupon);
        Globals.doBoldTextView(tvWallet);
        Globals.doBoldTextView(tvAmountPayable);
        Globals.doBoldTextView(tvTotal);
        Globals.doBoldTextView(tvCouponApplied);

        arrCoupons = new ArrayList<>();
        user_property_request_id = getIntent().getIntExtra(Constant.User_property_request_id, 0);
        property_price = getIntent().getIntExtra(Constant.Property_price, 0);
        seller_id = getIntent().getIntExtra(Constant.Seller_id, 0);
        property_id = getIntent().getIntExtra(Constant.Property_id, 0);

        llCouponApplied.setVisibility(View.GONE);

        calculationPrice();

        chkBalance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    wallet_balance = main_Wallet;
                } else {
                    wallet_balance = 0;
                }
                calculationPrice();
            }
        });

        if (Globals.isNetworkAvailable(this)) {
            getUserWalletMoney();
            bookingWiseCoupons();
        } else {
            finish();
            Toaster.shortToast(R.string.no_internet_msg);
        }

        showNoRecordFound(getString(R.string.no_coupon_found));

    }

    public void getUserWalletMoney() {
        JSONObject postData = HttpRequestHandler.getInstance().getUserWalletMoneyParam();

        if (postData != null) {

            new PostRequest(this, getString(R.string.userWalletMoney), postData, true, new PostRequest.OnPostServiceCallListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    UserWalletBalanceModel userWalletBalanceModel = new Gson().fromJson(response.toString(), UserWalletBalanceModel.class);
                    if (userWalletBalanceModel.status == 0 && userWalletBalanceModel.user_wallet_money != null) {
                        main_Wallet = Globals.checkInteger(userWalletBalanceModel.user_wallet_money.amount);
                    } else {
                        Toaster.shortToast(userWalletBalanceModel.message);
                    }
                    chkBalance.setText("₹ " + main_Wallet);
                }

                @Override
                public void onFailedToPostCall(int statusCode, String msg) {
                    Toaster.shortToast(msg);
                }
            }).execute();
        }
        Globals.hideKeyboard(this);
    }

    public void bookingWiseCoupons() {
        JSONObject postData = HttpRequestHandler.getInstance().getBookingWiseCouponsParam(user_property_request_id);

        if (postData != null) {

            new PostRequest(this, getString(R.string.bookingWiseCoupons), postData, true, new PostRequest.OnPostServiceCallListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    CouponsModel couponsModel = new Gson().fromJson(response.toString(), CouponsModel.class);
                    if (couponsModel.status == 0 && couponsModel.coupons != null && !couponsModel.coupons.isEmpty()) {
                        arrCoupons.addAll(couponsModel.coupons);
                        setAdapter();
                    } else {
                        showNoRecordFound(getString(R.string.no_coupon_found));
                        Toaster.shortToast(couponsModel.message);
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

    private void setAdapter() {
        hideNoRecordFound();
        if (adapterCouponList == null) {
            adapterCouponList = new AdapterCouponList(this);
        }
        adapterCouponList.doRefresh(arrCoupons, this);

        if (rvCoupon.getAdapter() == null) {
            rvCoupon.setHasFixedSize(false);
            rvCoupon.setLayoutManager(new LinearLayoutManager(this));
            rvCoupon.setItemAnimator(new DefaultItemAnimator());
            rvCoupon.setAdapter(adapterCouponList);
        }
    }

    private void showNoRecordFound(String no_data_found) {
        rvCoupon.setVisibility(View.GONE);
        if (tvNoDataFound.getVisibility() == View.GONE) {
            tvNoDataFound.setVisibility(View.VISIBLE);
            tvNoDataFound.setText(no_data_found);
        }
    }

    private void hideNoRecordFound() {
        rvCoupon.setVisibility(View.VISIBLE);
        if (tvNoDataFound.getVisibility() == View.VISIBLE)
            tvNoDataFound.setVisibility(View.GONE);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onCouponApply(int amount_type, int amount, String code, int id) {
        llCouponApplied.setVisibility(View.VISIBLE);
        tvCouponApplied.setText(code + " Applied!");
        coupon_price = amount_type == 0 ? (property_price * amount / 100) : amount;
        calculationPrice();
        coupon_id = id;
        coupon_amount_type = amount_type;
    }

    @OnClick(R.id.tv_remove)
    public void onCouponRemovedClicked() {
        for (int i = 0; i < arrCoupons.size(); i++) {
            arrCoupons.get(i).isApplied = false;
        }
        setAdapter();

        llCouponApplied.setVisibility(View.GONE);
        coupon_price = 0;
        coupon_id = 0;
        coupon_amount_type = 0;
        calculationPrice();
    }


    @SuppressLint("SetTextI18n")
    private void calculationPrice() {
        tvRent.setText("₹ " + property_price);
        if (wallet_balance > 0) {
            llWallet.setVisibility(View.VISIBLE);
            vwWallet.setVisibility(View.VISIBLE);
            tvWallet.setText("- ₹ " + wallet_balance);
        } else {
            llWallet.setVisibility(View.GONE);
            vwWallet.setVisibility(View.GONE);
        }

        if (coupon_price > 0) {
            llCoupon.setVisibility(View.VISIBLE);
            vwCoupon.setVisibility(View.VISIBLE);
            tvCoupon.setText("₹ " + coupon_price);
        } else {
            llCoupon.setVisibility(View.GONE);
            vwCoupon.setVisibility(View.GONE);
        }

        total = property_price - wallet_balance;// - coupon_price;
        tvAmountPayable.setText("₹ " + total);
        tvTotal.setText("₹ " + total);
    }

    @OnClick(R.id.btn_payment)
    public void onBtnPaymentClicked() {
        if (Globals.isNetworkAvailable(BookingPaymentActivity.this)) {
            doGenerateChecksum("" + total);
        } else {
            Toaster.shortToast(getString(R.string.no_internet_msg));
        }
    }

    String OrderId = "", CustId = "";

    private void doGenerateChecksum(String amount) {
        OrderId = "Order" + Globals.randomNumber();
        CustId = "Cust" + Globals.randomNumber();
        JSONObject postData = HttpRequestHandler.getInstance().doGenerateChecksumParam(OrderId, CustId, "1");//amount);//Todo : Remove this line
        if (postData != null) {

            new PostRequest(this, getString(R.string.main_url), getString(R.string.generateChecksum), postData, true, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    CheckSumModel checkSumModel = new Gson().fromJson(response.toString(), CheckSumModel.class);
                    if (Globals.isNetworkAvailable(BookingPaymentActivity.this)) {
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
        Globals.hideKeyboard(this);
    }


    public void onStartTransaction(String CHECKSUMHASH, String amount) {
        PaytmPGService Service = PaytmPGService.getProductionService();

        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put(Constant.MID, Constant.MID_Value);
        paramMap.put(Constant.ORDER_ID, OrderId);
        paramMap.put(Constant.CUST_ID, CustId);
        paramMap.put(Constant.INDUSTRY_TYPE_ID, Constant.INDUSTRY_TYPE_ID_Value);
        paramMap.put(Constant.CHANNEL_ID, Constant.CHANNEL_ID_Value);
        paramMap.put(Constant.TXN_AMOUNT, "1");//TODO : Replace it amount
        paramMap.put(Constant.WEBSITE, Constant.WEBSITE_Value);
        paramMap.put(Constant.CALLBACK_URL, Constant.CALLBACK_URL_Value + OrderId);
        paramMap.put(Constant.CHECKSUMHASH, CHECKSUMHASH);

        PaytmOrder Order = new PaytmOrder(paramMap);

        Service.initialize(Order, null);

        Service.startPaymentTransaction(this, true, true,
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
                            makeOrder(inResponse.containsKey("BANKTXNID") ? inResponse.getString("BANKTXNID") : "");

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


    public void makeOrder(String paymentId) {
        JSONObject postData = HttpRequestHandler.getInstance().makeOrderDataParam(user_property_request_id, seller_id, property_id, coupon_id, coupon_amount_type, coupon_price,
                wallet_balance, "Property booked", paymentId, "Paid");

        if (postData != null) {

            new PostRequest(this, getString(R.string.makeOrder), postData, true, new PostRequest.OnPostServiceCallListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    CouponsModel couponsModel = new Gson().fromJson(response.toString(), CouponsModel.class);
                    if (couponsModel.status == 0) {
                        onBackPressed();
                    }
                    Toaster.shortToast(couponsModel.message);
                }

                @Override
                public void onFailedToPostCall(int statusCode, String msg) {
                    Toaster.shortToast(msg);
                }
            }).execute();
        }
        Globals.hideKeyboard(this);
    }

}