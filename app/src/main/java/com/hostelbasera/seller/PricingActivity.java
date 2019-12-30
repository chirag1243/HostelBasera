package com.hostelbasera.seller;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hostelbasera.R;
import com.hostelbasera.model.CheckSellerPaymentDataModel;
import com.hostelbasera.utility.BaseActivity;
import com.hostelbasera.utility.Constant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PricingActivity extends BaseActivity {

    @BindView(R.id.rv_pricing)
    RecyclerView rvPricing;
    AdapterPricing adapterPricing;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.img_share)
    ImageView imgShare;

    ArrayList<CheckSellerPaymentDataModel.PriceBlockDetails> arrPriceBlockDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pricing);
        ButterKnife.bind(this);
        toolbarTitle.setText(getString(R.string.pricing));
        imgBack.setVisibility(View.VISIBLE);
        arrPriceBlockDetails = new ArrayList<>();
        arrPriceBlockDetails = (ArrayList<CheckSellerPaymentDataModel.PriceBlockDetails>) getIntent().getSerializableExtra(Constant.ArrPriceBlockDetails);
        if (arrPriceBlockDetails != null && !arrPriceBlockDetails.isEmpty()) {
            setAdapter();
        }else {
            onBackPressed();
        }
    }

   /* public void getPriceBlock() {
        JSONObject postData = HttpRequestHandler.getInstance().getPriceBlockParam();

        if (postData != null) {
            new PostRequest(this, getString(R.string.getPriceBlock), postData, true,
                    new PostRequest.OnPostServiceCallListener() {
                        @Override
                        public void onSucceedToPostCall(JSONObject response) {
                            PriceBlockDetailModel priceBlockDetailModel = new Gson().fromJson(response.toString(), PriceBlockDetailModel.class);
                            if (priceBlockDetailModel.status == 0) {
                                arrPriceBlockDetail = priceBlockDetailModel.priceBlockDetail;
                                if (arrPriceBlockDetail != null && !arrPriceBlockDetail.isEmpty()) {
                                    setAdapter();
                                }
                            } else
                                Toaster.shortToast(priceBlockDetailModel.message);
                        }

                        @Override
                        public void onFailedToPostCall(int statusCode, String msg) {
                            Toaster.shortToast(msg);
                        }
                    }).execute();
        }
        Globals.hideKeyboard(this);
    }*/

    private void setAdapter() {
        if (adapterPricing == null) {
            adapterPricing = new AdapterPricing(this);
        }
        adapterPricing.doRefresh(arrPriceBlockDetails);

        if (rvPricing.getAdapter() == null) {
            rvPricing.setHasFixedSize(false);
            rvPricing.setLayoutManager(new GridLayoutManager(this, Constant.GRID_SPAN));
            rvPricing.setItemAnimator(new DefaultItemAnimator());
            rvPricing.setAdapter(adapterPricing);
        }
    }

    @OnClick(R.id.img_back)
    public void onViewClicked() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
