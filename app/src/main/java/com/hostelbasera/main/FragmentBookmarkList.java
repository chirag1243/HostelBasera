package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hostelbasera.R;
import com.hostelbasera.apis.HttpRequestHandler;
import com.hostelbasera.apis.PostRequest;
import com.hostelbasera.model.BookmarkDetailModel;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.Globals;
import com.hostelbasera.utility.Toaster;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FragmentBookmarkList extends Fragment {

    View view;
    @BindView(R.id.rv_bookmark_list)
    RecyclerView rvBookmarkList;
    Globals globals;

    BookmarkDetailModel bookmarkDetailModel;
    AdapterBookmarkList adapterBookmarkList;

    public static FragmentBookmarkList newInstance(/*AllCategoriesDetailModel model*/) {
        FragmentBookmarkList fragment = new FragmentBookmarkList();
//        fragment.allCategoriesModel = model;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_bookmark_list, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        globals = ((Globals) getContext().getApplicationContext());

        if (Globals.isNetworkAvailable(getActivity())) {
            getBookmarkData();
        } else {
            Toaster.shortToast(R.string.no_internet_msg);
        }
    }

    public void getBookmarkData() {
        JSONObject postData = HttpRequestHandler.getInstance().getFilterListParam();

        if (postData != null) {
            new PostRequest(getActivity(), getString(R.string.getBookmarkData),
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
        Globals.hideKeyboard(getActivity());
    }

    private void setAdapter() {
        if (adapterBookmarkList == null) {
            adapterBookmarkList = new AdapterBookmarkList(getActivity());
        }
        adapterBookmarkList.doRefresh(bookmarkDetailModel.BookmarkDetails);

        if (rvBookmarkList.getAdapter() == null) {
            rvBookmarkList.setHasFixedSize(false);
            rvBookmarkList.setLayoutManager(new LinearLayoutManager(getActivity()));
            rvBookmarkList.setItemAnimator(new DefaultItemAnimator());
            rvBookmarkList.setAdapter(adapterBookmarkList);
        }

        adapterBookmarkList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getActivity(), HostelDetailActivity.class)
                        .putExtra(Constant.Property_id, bookmarkDetailModel.BookmarkDetails.get(position).property_id)
                        .putExtra(Constant.Property_name, bookmarkDetailModel.BookmarkDetails.get(position).property_name));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}


