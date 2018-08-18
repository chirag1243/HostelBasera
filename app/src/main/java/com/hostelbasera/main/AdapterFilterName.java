package com.hostelbasera.main;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.hostelbasera.R;
import com.hostelbasera.model.FilterModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterFilterName  extends RecyclerView.Adapter<AdapterFilterName.ViewHolder> {

    private ArrayList<FilterModel.FilterDetail> mValues;
    private final FilterActivity filterActivity;
    private AdapterView.OnItemClickListener onItemClickListener;
    private int isSelected = 0;

    public AdapterFilterName(FilterActivity activity) {
        filterActivity = activity;
    }

    public void doRefresh(ArrayList<FilterModel.FilterDetail> filtersOfFullCatalogsDetails) {
        mValues = filtersOfFullCatalogsDetails;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_name_item, parent, false);
        return new ViewHolder(view, this);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AdapterFilterName adapterStoreCatlogDetail;
        @BindView(R.id.tv_filter_name)
        TextView tvFilterName;
        @BindView(R.id.view_blue)
        View viewBlue;

        public ViewHolder(View itemView, AdapterFilterName adapterStoreCatlogDetail) {
            super(itemView);
            this.adapterStoreCatlogDetail = adapterStoreCatlogDetail;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void setDataToView(FilterModel.FilterDetail mItem, ViewHolder holder, int position) {
            holder.tvFilterName.setText(mItem.name);

            if (isSelected == position) {
                holder.tvFilterName.setBackgroundColor(Color.parseColor("#ffffff"));
                holder.viewBlue.setVisibility(View.VISIBLE);
            } else {
                holder.tvFilterName.setBackgroundColor(ContextCompat.getColor(filterActivity, R.color.filter_name_bg));
                holder.viewBlue.setVisibility(View.GONE);
            }

            holder.tvFilterName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isSelected = position;
                    mItem.isSelected = !mItem.isSelected;
                    notifyDataSetChanged();
                    filterActivity.setFilterAdapter(position);
                }
            });
        }

        @Override
        public void onClick(View v) {
            adapterStoreCatlogDetail.onItemHolderClick(ViewHolder.this);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setDataToView(mValues.get(position), holder, position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void onItemHolderClick(ViewHolder holder) {
        if (onItemClickListener != null)
            onItemClickListener.onItemClick(null, holder.itemView, holder.getAdapterPosition(), holder.getItemId());
    }
}


