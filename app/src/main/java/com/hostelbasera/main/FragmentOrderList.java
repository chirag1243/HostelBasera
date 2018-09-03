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

import com.google.gson.Gson;
import com.hostelbasera.R;
import com.hostelbasera.apis.HttpRequestHandler;
import com.hostelbasera.apis.PostRequest;
import com.hostelbasera.model.BookmarkDetailModel;
import com.hostelbasera.model.OrderDetailModel;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.Globals;
import com.hostelbasera.utility.Toaster;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentOrderList extends Fragment {

    View view;
    @BindView(R.id.rv_order_list)
    RecyclerView rvOrderList;
    Globals globals;

    ArrayList<OrderDetailModel.OrderDetails> arOrderDetailsArrayList;
    AdapterOrderList adapterOrderList;


    public static FragmentOrderList newInstance() {
        FragmentOrderList fragment = new FragmentOrderList();
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
        globals = ((Globals) getContext().getApplicationContext());

        if (Globals.isNetworkAvailable(getActivity())) {
            getOrderListData();
        } else {
            Toaster.shortToast(R.string.no_internet_msg);
        }
    }

    public void getOrderListData() {
        JSONObject postData = HttpRequestHandler.getInstance().getOrderDataParam();

        if (postData != null) {
            new PostRequest(getActivity(), getString(R.string.getOrderData),
                    postData, true, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    OrderDetailModel orderDetailModel = new Gson().fromJson(response.toString(), OrderDetailModel.class);
                    if (orderDetailModel.status == 0 && orderDetailModel.OrderDetails != null && !orderDetailModel.OrderDetails.isEmpty()) {
                        arOrderDetailsArrayList = orderDetailModel.OrderDetails;
                        setAdapter();
                    } else {
                        Toaster.shortToast(orderDetailModel.message);
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
        if (adapterOrderList == null) {
            adapterOrderList = new AdapterOrderList(getActivity());
        }
        adapterOrderList.doRefresh(arOrderDetailsArrayList);

        if (rvOrderList.getAdapter() == null) {
            rvOrderList.setHasFixedSize(false);
            rvOrderList.setLayoutManager(new LinearLayoutManager(getActivity()));
            rvOrderList.setItemAnimator(new DefaultItemAnimator());
            rvOrderList.setAdapter(adapterOrderList);
        }

        adapterOrderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getActivity(), HostelDetailActivity.class)
                        .putExtra(Constant.Property_id, arOrderDetailsArrayList.get(position).property_id)
                        .putExtra(Constant.Property_name, arOrderDetailsArrayList.get(position).property_name));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
