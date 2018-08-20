package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hostelbasera.R;
import com.hostelbasera.apis.HttpRequestHandler;
import com.hostelbasera.apis.PostRequest;
import com.hostelbasera.model.BookmarkDetailModel;
import com.hostelbasera.utility.BaseActivity;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.Globals;
import com.hostelbasera.utility.PaginationProgressBarAdapter;
import com.hostelbasera.utility.Toaster;
import com.paginate.Paginate;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BookmarkListActivity extends BaseActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.rv_bookmark_list)
    RecyclerView rvBookmarkList;
    Globals globals;

    BookmarkDetailModel bookmarkDetailModel;
    ArrayList<BookmarkDetailModel.BookmarkDetails> arrBookmarkDetailsArrayList;
    AdapterBookmarkList adapterBookmarkList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_list);
        ButterKnife.bind(this);
        init();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        globals = ((Globals) this.getApplicationContext());
        imgBack.setVisibility(View.VISIBLE);
        arrBookmarkDetailsArrayList = new ArrayList<>();
        toolbarTitle.setText("Bookmarks");

        if (Globals.isNetworkAvailable(this)) {
            getBookmarkData();
        } else {
            Toaster.shortToast(R.string.no_internet_msg);
        }
    }

    public void getBookmarkData() {
        JSONObject postData = HttpRequestHandler.getInstance().getFilterListParam();

        if (postData != null) {
            new PostRequest(this, getString(R.string.getBookmarkData),
                    postData, true, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    bookmarkDetailModel = new Gson().fromJson(response.toString(), BookmarkDetailModel.class);
                    if (bookmarkDetailModel.status == 0 && bookmarkDetailModel.BookmarkDetails != null && !bookmarkDetailModel.BookmarkDetails.isEmpty()) {
                        setAdapter();
                    } else {
                        Toaster.shortToast(bookmarkDetailModel.message);
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
        if (adapterBookmarkList == null) {
            adapterBookmarkList = new AdapterBookmarkList(this);
        }
        adapterBookmarkList.doRefresh(arrBookmarkDetailsArrayList);

        if (rvBookmarkList.getAdapter() == null) {
            rvBookmarkList.setHasFixedSize(false);
            rvBookmarkList.setLayoutManager(new LinearLayoutManager(this));
            rvBookmarkList.setItemAnimator(new DefaultItemAnimator());
            rvBookmarkList.setAdapter(adapterBookmarkList);
        }

        adapterBookmarkList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(BookmarkListActivity.this, HostelDetailActivity.class).putExtra(Constant.Property_id, arrBookmarkDetailsArrayList.get(position).property_id));
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
}
