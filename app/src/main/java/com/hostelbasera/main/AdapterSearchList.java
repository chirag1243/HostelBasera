package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.hostelbasera.R;
import com.hostelbasera.model.SearchModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterSearchList extends RecyclerView.Adapter<AdapterSearchList.ViewHolder> {

    private ArrayList<SearchModel.PropertyDetail> mValues;
    private final Context mContext;
    private AdapterView.OnItemClickListener onItemClickListener;

    AdapterSearchList(Context context) {
        mContext = context;
    }

    public void doRefresh(ArrayList<SearchModel.PropertyDetail> catalogsDetails) {
        mValues = catalogsDetails;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item, parent, false);
        return new ViewHolder(view, this);
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AdapterSearchList adapterSearchList;
        @BindView(R.id.tv_property_name)
        TextView tvPropertyName;
        @BindView(R.id.view_border)
        View viewBorder;

        public ViewHolder(View itemView, AdapterSearchList adapterSearchList) {
            super(itemView);
            this.adapterSearchList = adapterSearchList;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        void setDataToView(SearchModel.PropertyDetail mItem, ViewHolder holder, int position) {
            if (position == mValues.size() - 1)
                viewBorder.setVisibility(View.GONE);
            else
                viewBorder.setVisibility(View.VISIBLE);

            holder.tvPropertyName.setText(mItem.property_name);
        }

        @Override
        public void onClick(View v) {
            adapterSearchList.onItemHolderClick(ViewHolder.this);
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




