package com.hostelbasera.main;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.hostelbasera.R;
import com.hostelbasera.model.FilterModel;
import com.hostelbasera.utility.BaseActivity;
import com.hostelbasera.utility.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FilterActivity extends BaseActivity {

    public FilterModel filterModel;
    AdapterFilterName adapterFilterName;
    public AdapterFilter adapterFilter;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.rv_filter_name)
    public RecyclerView rvFilterName;
    @BindView(R.id.rv_filter)
    RecyclerView rvFilter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);

        toolbarTitle.setText(getString(R.string.filterby));
        if (getIntent() != null) {
            filterModel = (FilterModel) getIntent().getSerializableExtra(Constant.FilterModel);
            setFilterNameAdapter();
            setFilterAdapter(0);
        }
    }

    public void setFilterNameAdapter() {
        if (adapterFilterName == null) {
            adapterFilterName = new AdapterFilterName(this);
        }
        adapterFilterName.doRefresh(filterModel.filterDetail);

        if (rvFilterName.getAdapter() == null) {
            rvFilterName.setHasFixedSize(false);
            rvFilterName.setLayoutManager(new LinearLayoutManager(this));
            rvFilterName.setItemAnimator(new DefaultItemAnimator());
            rvFilterName.setAdapter(adapterFilterName);
        }
    }

    public void setFilterAdapter(int position) {
        if (adapterFilter == null) {
            adapterFilter = new AdapterFilter(this);
        }
        adapterFilter.doRefresh(filterModel.filterDetail.get(position).data, filterModel.filterDetail.get(position).filterType);

        if (rvFilter.getAdapter() == null) {
            rvFilter.setHasFixedSize(false);
            rvFilter.setLayoutManager(new LinearLayoutManager(this));
            rvFilter.setItemAnimator(new DefaultItemAnimator());
            rvFilter.setAdapter(adapterFilter);
        }
    }

    @OnClick(R.id.img_back)
    public void onClickBack() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R.id.tv_clear_all)
    public void onClearAllClicked() {
        for (int i = 0; i < filterModel.filterDetail.size(); i++) {
            for (int j = 0; j < filterModel.filterDetail.get(i).data.size(); j++) {
                filterModel.filterDetail.get(i).data.get(j).isSelected = false;
            }
        }
        setResult(Activity.RESULT_OK, new Intent(this, CategoryListActivity.class).putExtra(Constant.FilterModel, filterModel).putExtra(Constant.IsClearAll, true));
        finish();
    }

    @OnClick(R.id.tv_apply)
    public void onApplyClicked() {
        setResult(Activity.RESULT_OK, new Intent(this, CategoryListActivity.class).putExtra(Constant.FilterModel, filterModel).putExtra(Constant.IsClearAll, false));
        finish();
    }
}
