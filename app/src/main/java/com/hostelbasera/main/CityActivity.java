package com.hostelbasera.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hostelbasera.R;
import com.hostelbasera.apis.HttpRequestHandler;
import com.hostelbasera.apis.PostRequest;
import com.hostelbasera.model.CityDetailModel;
import com.hostelbasera.utility.BaseActivity;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.Globals;
import com.hostelbasera.utility.Toaster;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CityActivity extends BaseActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.tv_popular_cities)
    TextView tvPopularCities;
    @BindView(R.id.vw_popular_cities)
    View vwPopularCities;
    @BindView(R.id.rv_popular_cities)
    RecyclerView rvPopularCities;
    @BindView(R.id.tv_all_cities)
    TextView tvAllCities;
    @BindView(R.id.vw_all_cities)
    View vwAllCities;
    @BindView(R.id.rv_all_cities)
    RecyclerView rvAllCities;
    @BindView(R.id.ll_main)
    LinearLayout llMain;

    Globals globals;
    CityDetailModel cityDetailModel;

    ArrayList<CityDetailModel.CityDetail> arrPopularCityDetail;
    ArrayList<CityDetailModel.CityDetail> arrCityDetail;

    AdapterPopularCityList adapterPopularCityList;
    AdapterCityList adapterCityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        ButterKnife.bind(this);
        globals = ((Globals) this.getApplicationContext());

        if (Globals.isNetworkAvailable(this)) {
            getCityListData();
        } else {
            onBackPressed();
            Toaster.shortToast(R.string.no_internet_msg);
        }
    }

    public void getCityListData() {
        JSONObject postData = HttpRequestHandler.getInstance().getCityListParam();

        if (postData != null) {
            new PostRequest(this, getString(R.string.getCityList),
                    postData, true, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    cityDetailModel = new Gson().fromJson(response.toString(), CityDetailModel.class);
                    if (cityDetailModel.status == 0 && cityDetailModel.cityDetail != null && !cityDetailModel.cityDetail.isEmpty()) {
                        llMain.setVisibility(View.VISIBLE);
                        arrCityDetail = cityDetailModel.cityDetail;
                        doAscendingCity();

                        CityDetailModel.CityDetail detail = new CityDetailModel.CityDetail();
                        detail.city_name = getString(R.string.all);
                        detail.city_id = 0;
                        detail.is_popular = false;
                        detail.img = "";
                        arrCityDetail.add(0, detail);

                        doSetPopularCity();
                        setCityAdapter();
                    } else {
                        Toaster.shortToast(cityDetailModel.message);
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

    void doAscendingCity() {
        Collections.sort(arrCityDetail, new Comparator<CityDetailModel.CityDetail>() {
            @Override
            public int compare(CityDetailModel.CityDetail lhs, CityDetailModel.CityDetail rhs) {
                return lhs.city_name.compareTo(rhs.city_name);
            }
        });
    }

    void doSetPopularCity() {
        arrPopularCityDetail = new ArrayList<>();
        for (int i = 0; i < arrCityDetail.size(); i++) {
            if (arrCityDetail.get(i).is_popular) {
                arrPopularCityDetail.add(arrCityDetail.get(i));
            }
        }
        if (!arrPopularCityDetail.isEmpty()) {
            setPopularCityAdapter();
        } else {
            tvPopularCities.setVisibility(View.GONE);
            vwPopularCities.setVisibility(View.GONE);
            rvPopularCities.setVisibility(View.GONE);
        }
    }

    private void setPopularCityAdapter() {
        if (adapterPopularCityList == null) {
            adapterPopularCityList = new AdapterPopularCityList(this);
        }
        adapterPopularCityList.doRefresh(arrPopularCityDetail);

        if (rvPopularCities.getAdapter() == null) {
            rvPopularCities.setHasFixedSize(false);
            rvPopularCities.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rvPopularCities.setItemAnimator(new DefaultItemAnimator());
            rvPopularCities.setAdapter(adapterPopularCityList);
        }

        adapterPopularCityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                globals.setCityId(arrPopularCityDetail.get(position).city_id);
                setResult(Constant.CityCode, new Intent(CityActivity.this, DashboardActivity.class)
                        .putExtra(Constant.City_Name, arrPopularCityDetail.get(position).city_name));
                finish();
            }
        });
    }

    private void setCityAdapter() {
        if (adapterCityList == null) {
            adapterCityList = new AdapterCityList(this);
        }
        adapterCityList.doRefresh(arrCityDetail);

        if (rvAllCities.getAdapter() == null) {
            rvAllCities.setHasFixedSize(false);
            rvAllCities.setLayoutManager(new LinearLayoutManager(this));
            rvAllCities.setItemAnimator(new DefaultItemAnimator());
            rvAllCities.setAdapter(adapterCityList);
        }

        adapterCityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                globals.setCityId(arrCityDetail.get(position).city_id);
                setResult(Constant.CityCode, new Intent(CityActivity.this, DashboardActivity.class)
                        .putExtra(Constant.City_Name, arrCityDetail.get(position).city_name));
                finish();
            }
        });
    }

    @OnClick(R.id.img_back)
    public void onViewClicked() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK, new Intent(this, DashboardActivity.class));
        finish();
    }
}
