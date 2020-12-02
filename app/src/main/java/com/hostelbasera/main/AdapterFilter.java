package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hostelbasera.R;
import com.hostelbasera.model.FilterModel;
import com.hostelbasera.utility.Constant;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterFilter extends RecyclerView.Adapter<AdapterFilter.ViewHolder> {

    private ArrayList<FilterModel.Data> mValues;
    private final Context mContext;
    private AdapterView.OnItemClickListener onItemClickListener;
    private int filterType;

    public AdapterFilter(Context context) {
        mContext = context;
    }

    public void doRefresh(ArrayList<FilterModel.Data> data, int filterType) {
        mValues = data;
        this.filterType = filterType;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_item, parent, false);
        return new ViewHolder(view, this);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AdapterFilter adapterStoreCatlogDetail;
        @BindView(R.id.img_filter)
        ImageView imgFilter;
        @BindView(R.id.tv_filter_name)
        TextView tv_filter;
        @BindView(R.id.ll_filter)
        LinearLayout llFilter;

        public ViewHolder(View itemView, AdapterFilter adapterStoreCatlogDetail) {
            super(itemView);
            this.adapterStoreCatlogDetail = adapterStoreCatlogDetail;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        void setDataToView(ViewHolder holder, int position) {
            holder.tv_filter.setText("" + mValues.get(position).property_size);
            switch (filterType) {
                case Constant.Size:
                    holder.tv_filter.setText("" + mValues.get(position).property_size);
                    break;
                case Constant.Property_Types:
                    holder.tv_filter.setText("" + mValues.get(position).property_type);
                    break;
                case Constant.Types:
                    holder.tv_filter.setText("" + mValues.get(position).type_name);
                    break;
                case Constant.Prices:
                    holder.tv_filter.setText("" + mValues.get(position).price);
                    break;
            }

            if (mValues.get(position).isSelected)
                holder.imgFilter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.filter_seletet_fill));
            else
                holder.imgFilter.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.filter_seletet_unfill));


            holder.llFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mValues.get(position).isSelected = !mValues.get(position).isSelected;
                    notifyDataSetChanged();
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
        holder.setDataToView(holder, position);
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


