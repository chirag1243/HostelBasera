package com.hostelbasera.seller;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hostelbasera.R;
import com.hostelbasera.apis.HttpRequestHandler;
import com.hostelbasera.apis.PostRequest;
import com.hostelbasera.main.DashboardActivity;
import com.hostelbasera.main.LoginActivity;
import com.hostelbasera.model.SellerDropdownModel;
import com.hostelbasera.model.UserDetailModel;
import com.hostelbasera.utility.BaseActivity;
import com.hostelbasera.utility.Globals;
import com.hostelbasera.utility.Toaster;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddHostelPGActivity extends BaseActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.img_share)
    ImageView imgShare;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.sp_property)
    Spinner spProperty;
    @BindView(R.id.sp_seller)
    Spinner spSeller;
    @BindView(R.id.edt_add_comment)
    EditText edtAddComment;
    @BindView(R.id.sp_category)
    Spinner spCategory;
    @BindView(R.id.sp_type_of_property)
    Spinner spTypeOfProperty;
    @BindView(R.id.sp_size_of_property)
    Spinner spSizeOfProperty;
    @BindView(R.id.edt_email)
    EditText edtEmail;
    @BindView(R.id.edt_address)
    EditText edtAddress;
    @BindView(R.id.rv_contact)
    RecyclerView rvContact;
    @BindView(R.id.edt_description)
    EditText edtDescription;
    @BindView(R.id.sp_state)
    Spinner spState;
    @BindView(R.id.sp_city)
    Spinner spCity;
    @BindView(R.id.sp_facility)
    Spinner spFacility;
    @BindView(R.id.edt_water_timings)
    EditText edtWaterTimings;
    @BindView(R.id.edt_open_hours)
    EditText edtOpenHours;
    @BindView(R.id.rv_images)
    RecyclerView rvImages;
    @BindView(R.id.edt_price)
    EditText edtPrice;
    @BindView(R.id.rv_rooms)
    RecyclerView rvRooms;
    @BindView(R.id.btn_add_hostel_pg)
    Button btnAddHostelPg;

    Globals globals;
    SellerDropdownModel.SellerDropdownDetail sellerDropdownDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hostel_pg);
        ButterKnife.bind(this);
        init();
    }

    public void init() {
        imgBack.setVisibility(View.VISIBLE);
        globals = ((Globals) getApplicationContext());
        toolbarTitle.setText(getString(R.string.add_hostel_pg));

        if (Globals.isNetworkAvailable(this)) {
            getSellerDropdownData();
        } else {
            Toaster.shortToast(R.string.no_internet_msg);
            finish();
        }
    }

    public void getSellerDropdownData() {
        JSONObject postData = HttpRequestHandler.getInstance().getSellerDropdownParam();

        if (postData != null) {
            new PostRequest(this, getString(R.string.getSellerDropdown), postData, true,
                    new PostRequest.OnPostServiceCallListener() {
                        @Override
                        public void onSucceedToPostCall(JSONObject response) {
                            SellerDropdownModel sellerDropdownModel = new Gson().fromJson(response.toString(), SellerDropdownModel.class);
                            if (sellerDropdownModel.status == 0) {
                                sellerDropdownDetail = sellerDropdownModel.sellerDropdownDetail;
                                setData();
                            }
                            Toaster.shortToast(sellerDropdownModel.message);
                        }

                        @Override
                        public void onFailedToPostCall(int statusCode, String msg) {
                            Toaster.shortToast(msg);
                        }
                    }).execute();
        }
        Globals.hideKeyboard(this);
    }

    public void setData(){

    }

    @OnClick(R.id.img_back)
    public void onImgBackClicked() {
        onBackPressed();
    }

    @OnClick(R.id.btn_add_hostel_pg)
    public void onBtnAddHostelPgClicked() {
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
