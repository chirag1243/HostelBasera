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
import com.hostelbasera.model.PropertyDetailModel;
import com.hostelbasera.utility.Globals;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterAmenities extends RecyclerView.Adapter<AdapterAmenities.ViewHolder> {

    private ArrayList<PropertyDetailModel.PropertyFacility> mValues;
    private final Context mContext;
    private AdapterView.OnItemClickListener onItemClickListener;
    Globals globals;

    AdapterAmenities(Context context) {
        mContext = context;
        globals = ((Globals) context.getApplicationContext());
    }

    public void doRefresh(ArrayList<PropertyDetailModel.PropertyFacility> arrPropertyFacility) {
        mValues = arrPropertyFacility;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.amenities_item, parent, false);
        return new ViewHolder(view, this);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AdapterAmenities adapterAmenities;

        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.tv_name)
        TextView tvName;

        ViewHolder(View itemView, AdapterAmenities adapterAmenities) {
            super(itemView);
            this.adapterAmenities = adapterAmenities;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        void setDataToView(PropertyDetailModel.PropertyFacility mItem, ViewHolder holder, int position) {

            tvName.setText(mItem.facility_name);

            Glide.with(mContext)
                    .load(mContext.getString(R.string.facility_url) + mItem.facility_icon_img)
                    .apply(new RequestOptions()
                            .fitCenter()
                            .placeholder(R.mipmap.ic_launcher)
                            .dontAnimate()
                            .priority(Priority.HIGH))
                    .into(image);

        }

        @Override
        public void onClick(View v) {
            adapterAmenities.onItemHolderClick(ViewHolder.this);
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




