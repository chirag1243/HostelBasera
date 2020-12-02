package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.hostelbasera.R;
import com.hostelbasera.model.CityDetailModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterPopularCityList extends RecyclerView.Adapter<AdapterPopularCityList.ViewHolder> {

    private ArrayList<CityDetailModel.CityDetail> mValues;
    private final Context mContext;
    private AdapterView.OnItemClickListener onItemClickListener;

    AdapterPopularCityList(Context context) {
        mContext = context;
    }

    public void doRefresh(ArrayList<CityDetailModel.CityDetail> arrBookmarkDetails) {
        mValues = arrBookmarkDetails;
        notifyDataSetChanged();
    }

    @Override
    public AdapterPopularCityList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_city_item, parent, false);
        return new AdapterPopularCityList.ViewHolder(view, this);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AdapterPopularCityList adapterPopularCityList;

        @BindView(R.id.img_city)
        ImageView imgCity;
        @BindView(R.id.tv_city)
        TextView tvCity;

        ViewHolder(View itemView, AdapterPopularCityList adapterPopularCityList) {
            super(itemView);
            this.adapterPopularCityList = adapterPopularCityList;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        void setDataToView(CityDetailModel.CityDetail mItem, AdapterPopularCityList.ViewHolder holder, int position) {
            tvCity.setText("" + mItem.city_name);
//            tvCity.setTypeface(tvCity.getTypeface(), Typeface.BOLD);

            Glide.with(mContext)
                    .load(mContext.getString(R.string.city_url) + mItem.img)
                    .apply(new RequestOptions()
                            .fitCenter()
                            .placeholder(R.mipmap.ic_launcher)
                            .dontAnimate()
                            .priority(Priority.HIGH))
                    .into(imgCity);
        }

        @Override
        public void onClick(View v) {
            adapterPopularCityList.onItemHolderClick(AdapterPopularCityList.ViewHolder.this);
        }
    }

    @Override
    public void onBindViewHolder(final AdapterPopularCityList.ViewHolder holder, int position) {
        holder.setDataToView(mValues.get(position), holder, position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void onItemHolderClick(AdapterPopularCityList.ViewHolder holder) {
        if (onItemClickListener != null)
            onItemClickListener.onItemClick(null, holder.itemView, holder.getAdapterPosition(), holder.getItemId());
    }
}