package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.hostelbasera.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentPolicyDetails extends Fragment {

    View view;
    @BindView(R.id.web_view)
    WebView webView;
    String html;

    public static FragmentPolicyDetails newInstance(String html) {
        FragmentPolicyDetails fragment = new FragmentPolicyDetails();
        fragment.html = html;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_policy_details, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        final String mimeType = "text/html";
        final String encoding = "UTF-8";
        webView.loadUrl("file:///android_asset/" + html);
//        webView.loadDataWithBaseURL("", html, mimeType, encoding, "");
    }


    @Override
    public void onResume() {
        super.onResume();
    }
}