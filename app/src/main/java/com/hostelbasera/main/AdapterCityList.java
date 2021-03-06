package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.hostelbasera.R;
import com.hostelbasera.model.CityDetailModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterCityList extends RecyclerView.Adapter<AdapterCityList.ViewHolder> {

    private ArrayList<CityDetailModel.CityDetail> mValues;
    private Context mContext;
    private AdapterView.OnItemClickListener onItemClickListener;

    AdapterCityList(Context context) {
        mContext = context;
    }

    public void doRefresh(ArrayList<CityDetailModel.CityDetail> arrBookmarkDetails) {
        mValues = arrBookmarkDetails;
        notifyDataSetChanged();
    }

    @Override
    public AdapterCityList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_item, parent, false);
        return new AdapterCityList.ViewHolder(view, this);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AdapterCityList adapterCityList;

        @BindView(R.id.tv_city)
        TextView tvCity;

        ViewHolder(View itemView, AdapterCityList adapterCityList) {
            super(itemView);
            this.adapterCityList = adapterCityList;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        void setDataToView(CityDetailModel.CityDetail mItem, AdapterCityList.ViewHolder holder, int position) {
            tvCity.setText("" + mItem.city_name);
            tvCity.setTypeface(tvCity.getTypeface(), Typeface.BOLD);
        }

        @Override
        public void onClick(View v) {
            adapterCityList.onItemHolderClick(AdapterCityList.ViewHolder.this);
        }
    }

    @Override
    public void onBindViewHolder(final AdapterCityList.ViewHolder holder, int position) {
        holder.setDataToView(mValues.get(position), holder, position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void onItemHolderClick(AdapterCityList.ViewHolder holder) {
        if (onItemClickListener != null)
            onItemClickListener.onItemClick(null, holder.itemView, holder.getAdapterPosition(), holder.getItemId());
    }
}
