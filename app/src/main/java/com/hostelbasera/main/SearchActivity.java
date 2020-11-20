package com.hostelbasera.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hostelbasera.R;
import com.hostelbasera.apis.HttpRequestHandler;
import com.hostelbasera.apis.PostRequest;
import com.hostelbasera.model.SearchModel;
import com.hostelbasera.utility.BaseActivity;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.Toaster;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends BaseActivity {

    @BindView(R.id.search_view)
    EditText edsearchView;
    @BindView(R.id.rv_search)
    RecyclerView rvSearch;
    @BindView(R.id.img_close)
    AppCompatImageView imgClose;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tv_no_data_found)
    TextView tvNoDataFound;

    AdapterSearchList adapterSearchList;
    SearchModel searchModel;
    ArrayList<SearchModel.PropertyDetail> arrCatalogsDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        edsearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edsearchView.getText().length() == 0) {
                    rvSearch.setVisibility(View.GONE);
                    tvNoDataFound.setVisibility(View.VISIBLE);
                    imgClose.setVisibility(View.GONE);
                } else
                    imgClose.setVisibility(View.VISIBLE);

                if (edsearchView.getText().length() > 1) {
                    rvSearch.setVisibility(View.GONE);
                    tvNoDataFound.setVisibility(View.VISIBLE);
                    getSearchData();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edsearchView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    onImgSearchClicked();
                    return true;
                }
                return false;
            }
        });
    }

    public void getSearchData() {
        progressBar.setVisibility(View.VISIBLE);
        tvNoDataFound.setVisibility(View.GONE);
        JSONObject postData = HttpRequestHandler.getInstance().getSearchDataParam(edsearchView.getText().toString().trim());


        if (postData != null) {
            new PostRequest(this, getString(R.string.searchProperty),
                    postData, false, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    progressBar.setVisibility(View.GONE);
                    searchModel = new Gson().fromJson(response.toString(), SearchModel.class);
                    if (searchModel.status == 0 && searchModel.is_valid_token) {
                        rvSearch.setVisibility(View.VISIBLE);
                        setListAdapter();
                    } else {
                        tvNoDataFound.setVisibility(View.VISIBLE);
//                        Toaster.shortToast(searchModel.message);
                    }
                }

                @Override
                public void onFailedToPostCall(int statusCode, String msg) {
                    progressBar.setVisibility(View.GONE);
                    tvNoDataFound.setVisibility(View.VISIBLE);
                    Toaster.shortToast(msg);
                }
            }).execute();
        }
//        Globals.hideKeyboard(this);
    }

    private void setListAdapter() {
        if (adapterSearchList == null) {
            adapterSearchList = new AdapterSearchList(this);
        }
        adapterSearchList.doRefresh(searchModel.propertyDetail);

        if (rvSearch.getAdapter() == null) {
            rvSearch.setHasFixedSize(false);
            rvSearch.setLayoutManager(new LinearLayoutManager(this));
            rvSearch.setItemAnimator(new DefaultItemAnimator());
            rvSearch.setAdapter(adapterSearchList);
        }

        adapterSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(SearchActivity.this, HostelDetailActivity.class)
                        .putExtra(Constant.Property_id, searchModel.propertyDetail.get(position).property_id)
                        .putExtra(Constant.Property_name, searchModel.propertyDetail.get(position).property_name));
            }
        });
    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @OnClick(R.id.img_back)
    public void onImgBackClicked() {
        onBackPressed();
    }

    @OnClick(R.id.img_close)
    public void onImgCloseClicked() {
        edsearchView.setText("");
        rvSearch.setVisibility(View.GONE);
    }

    @OnClick(R.id.img_search)
    public void onImgSearchClicked() {
        if (edsearchView.getText().toString().trim().isEmpty()) {
            Toaster.shortToast("Please enter text");
            return;
        }
        startActivity(new Intent(SearchActivity.this, SearchListActivity.class)
                .putExtra(Constant.Searchtext, edsearchView.getText().toString()));
    }

}
