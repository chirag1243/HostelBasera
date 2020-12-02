package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.hostelbasera.R;
import com.hostelbasera.model.OrderDetailModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterOrderList extends RecyclerView.Adapter<AdapterOrderList.ViewHolder> {

    private ArrayList<OrderDetailModel.OrderDetails> mValues;
    private final Context mContext;
    private AdapterView.OnItemClickListener onItemClickListener;

    public AdapterOrderList(Context context) {
        mContext = context;
    }

    public void doRefresh(ArrayList<OrderDetailModel.OrderDetails> arrPropertyDetails) {
        mValues = arrPropertyDetails;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new ViewHolder(view, this);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AdapterOrderList adapterOrderList;

        @BindView(R.id.img_product)
        ImageView imgProduct;
        @BindView(R.id.img_place_holder)
        ImageView imgPlaceHolder;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.simpleRatingBar)
        RatingBar simpleRatingBar;
        @BindView(R.id.tv_location)
        TextView tvLocation;
        @BindView(R.id.tv_price)
        TextView tvPrice;

        ViewHolder(View itemView, AdapterOrderList adapterOrderList) {
            super(itemView);
            this.adapterOrderList = adapterOrderList;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        void setDataToView(OrderDetailModel.OrderDetails mItem, ViewHolder holder, int position) {

            tvName.setText("" + mItem.property_name);
            tvName.setTypeface(tvName.getTypeface(), Typeface.BOLD);
            tvPrice.setText("₹ " + mItem.price);
            tvPrice.setTypeface(tvPrice.getTypeface(), Typeface.BOLD);
            tvLocation.setText("" + mItem.city_name);

            simpleRatingBar.setRating(mItem.rating);

            imgPlaceHolder.setVisibility(View.VISIBLE);

            Glide.with(mContext)
                    .load(mContext.getString(R.string.image_url) + mItem.image).apply(new RequestOptions().dontAnimate())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.imgPlaceHolder.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(holder.imgProduct);

        }

        @Override
        public void onClick(View v) {
            adapterOrderList.onItemHolderClick(ViewHolder.this);
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

