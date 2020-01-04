package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
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
import com.hostelbasera.model.GetPropertyDetailModel;
import com.hostelbasera.utility.Globals;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterCategoryList extends RecyclerView.Adapter<AdapterCategoryList.ViewHolder> {

    private ArrayList<GetPropertyDetailModel.PropertyDetail> mValues;
    private final Context mContext;
    private AdapterView.OnItemClickListener onItemClickListener;
    boolean isNearMe,isSearchList;

    AdapterCategoryList(Context context) {
        mContext = context;
    }

    public void doRefresh(ArrayList<GetPropertyDetailModel.PropertyDetail> arrPropertyDetails, boolean isNearMe,boolean isSearchList) {
        mValues = arrPropertyDetails;
        this.isNearMe = isNearMe;
        this.isSearchList = isSearchList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_item, parent, false);
        return new ViewHolder(view, this);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AdapterCategoryList adapterCategoryList;

        @BindView(R.id.img_product)
        ImageView imgProduct;
        @BindView(R.id.img_place_holder)
        ImageView imgPlaceHolder;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.simpleRatingBar)
        RatingBar simpleRatingBar;
        @BindView(R.id.tv_location)
        TextView tvLocation;
        @BindView(R.id.vw_right_border)
        View vwRightBorder;
        @BindView(R.id.vw_bottom_border)
        View vwBottomBorder;

        @BindView(R.id.tv_area)
        TextView tvArea;
        @BindView(R.id.tv_boys_girls)
        TextView tvBoysGirls;
        @BindView(R.id.tv_distance)
        TextView tvDistance;

        ViewHolder(View itemView, AdapterCategoryList adapterCategoryList) {
            super(itemView);
            this.adapterCategoryList = adapterCategoryList;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        void setDataToView(GetPropertyDetailModel.PropertyDetail mItem, ViewHolder holder, int position) {

            if (position % 2 == 0) {
                vwRightBorder.setVisibility(View.VISIBLE);
            } else {
                vwRightBorder.setVisibility(View.GONE);
            }

            tvName.setText("" + mItem.property_name);
            tvName.setTypeface(tvName.getTypeface(), Typeface.BOLD);
            tvPrice.setText("₹ " + mItem.price);
            tvPrice.setTypeface(tvPrice.getTypeface(), Typeface.BOLD);
            if (mItem.property_area != null && !mItem.property_area.isEmpty()) {
                tvArea.setText(mItem.property_area);
            } else
                tvArea.setText("");

            if (isSearchList || isNearMe){
                tvBoysGirls.setVisibility(View.VISIBLE);
                Globals.doBoldTextView(tvBoysGirls);
                String category = "";
                if (mItem.property_category_id == 1) {
                    category = mContext.getString(R.string.boys);
                } else if (mItem.property_category_id == 2) {
                    category = mContext.getString(R.string.girls);
                } else if (mItem.property_category_id == 4) {
                    category = mContext.getString(R.string.both);
                }

                tvBoysGirls.setText(category);
            }else {
                tvBoysGirls.setVisibility(View.GONE);
            }

            if (isNearMe) {
                tvLocation.setVisibility(View.GONE);

                tvDistance.setVisibility(View.VISIBLE);
                tvDistance.setText(String.format("%.2f", mItem.distance)+"km");
            } else {
                tvLocation.setVisibility(View.VISIBLE);
                tvLocation.setText("" + mItem.city_name);
                //"" + (isNearMe && mItem.property_area != null && !mItem.property_area.isEmpty() ? mItem.property_area : mItem.city_name)
                tvLocation.setTypeface(tvLocation.getTypeface(), Typeface.BOLD);

                tvDistance.setVisibility(View.GONE);
            }
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
            adapterCategoryList.onItemHolderClick(ViewHolder.this);
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
