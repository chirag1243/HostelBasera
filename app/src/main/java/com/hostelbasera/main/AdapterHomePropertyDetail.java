package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
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
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.Globals;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterHomePropertyDetail extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<GetPropertyDetailModel.PropertyDetail> mValues;
    private final Context mContext;
    private AdapterView.OnItemClickListener onItemClickListener;

    public int TYPE_HEADER = 0, TYPE_BANNER = 1;
    public int TYPE_ITEM = 2;

    AdapterHomePropertyDetail(Context context) {
        mContext = context;
    }

    public void doRefresh(ArrayList<GetPropertyDetailModel.PropertyDetail> arrPropertyDetails) {
        mValues = arrPropertyDetails;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        if (viewType == TYPE_HEADER) {
            View v1 = inflater.inflate(R.layout.header_layout, viewGroup, false);
            viewHolder = new HeaderViewHolder(v1);
        } /*else if (viewType == TYPE_BANNER) {
            View v2 = inflater.inflate(R.layout.banner_layout, viewGroup, false);
            viewHolder = new HeaderBannerViewHolder(v2);
        } */else {
            View v3 = inflater.inflate(R.layout.home_hostel_item, viewGroup, false);
            viewHolder = new ItemViewHolder(v3);
        }
        return viewHolder;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnItemClickListener {
        private AdapterHomePropertyDetail adapterhomepropertydetail;

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
        @BindView(R.id.card_view1)
        CardView cardView1;

        @BindView(R.id.tv_area)
        TextView tvArea;

        @BindView(R.id.tv_boys_girls)
        TextView tvBoysGirls;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint("SetTextI18n")
        void setDataToView(GetPropertyDetailModel.PropertyDetail mItem, ItemViewHolder holder, int position) {

            tvName.setText("" + mItem.property_name);
            tvName.setTypeface(tvName.getTypeface(), Typeface.BOLD);
            tvPrice.setText("₹ " + mItem.price);
            tvPrice.setTypeface(tvPrice.getTypeface(), Typeface.BOLD);
            tvLocation.setText("" + mItem.city_name);
            tvLocation.setTypeface(tvLocation.getTypeface(), Typeface.BOLD);

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

            if (mItem.property_area != null && !mItem.property_area.isEmpty()) {
                tvArea.setVisibility(View.VISIBLE);
                tvArea.setText(mItem.property_area);
            } else
                tvArea.setVisibility(View.GONE);

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

            cardView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, HostelDetailActivity.class).putExtra(Constant.Property_id, mValues.get(position).property_id)
                            .putExtra(Constant.Property_name, mValues.get(position).property_name));
                }
            });

        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnItemClickListener {

        @BindView(R.id.tv_hostel_suggestion)
        TextView tvHostelSuggestion;
        @BindView(R.id.rv_banner)
        RecyclerView rvBanner;

        HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint("SetTextI18n")
        void setDataToView(GetPropertyDetailModel.PropertyDetail mItem, HeaderViewHolder holder, int position) {
            tvHostelSuggestion.setTypeface(tvHostelSuggestion.getTypeface(), Typeface.BOLD);

            if (mItem.banners != null && !mItem.banners.isEmpty()) {

                rvBanner.setVisibility(View.VISIBLE);

                AdapterBanners adapterDocuments = new AdapterBanners(mContext);

                rvBanner.setHasFixedSize(true);
                rvBanner.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                rvBanner.setAdapter(adapterDocuments);
                adapterDocuments.doRefresh(mItem.banners);
                adapterDocuments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                       /* if (!Globals.checkString(mItem.banners.get(position).url).isEmpty()) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mItem.banners.get(position).url));
                            mContext.startActivity(browserIntent);Todo : Check browser click
                        }*/
                    }
                });
            } else {
                rvBanner.setVisibility(View.GONE);
            }
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    }

    class HeaderBannerViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnItemClickListener {

        @BindView(R.id.rv_banner)
        RecyclerView rvBanner;

        HeaderBannerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint("SetTextI18n")
        void setDataToView(GetPropertyDetailModel.PropertyDetail mItem, HeaderBannerViewHolder holder, int position) {
            if (mItem.banners != null && !mItem.banners.isEmpty()) {

                rvBanner.setVisibility(View.VISIBLE);

                AdapterBanners adapterDocuments = new AdapterBanners(mContext);

                rvBanner.setHasFixedSize(true);
                rvBanner.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                rvBanner.setAdapter(adapterDocuments);
                adapterDocuments.doRefresh(mItem.banners);
                adapterDocuments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                       /* if (!Globals.checkString(mItem.banners.get(position).url).isEmpty()) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mItem.banners.get(position).url));
                            mContext.startActivity(browserIntent);Todo : Check browser click
                        }*/
                    }
                });
            } else {
                rvBanner.setVisibility(View.GONE);
            }

        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_HEADER) {
            HeaderViewHolder vh1 = (HeaderViewHolder) holder;
            vh1.setDataToView(mValues.get(position), vh1, position);
        } /*else if (holder.getItemViewType() == TYPE_BANNER) {
            HeaderBannerViewHolder vh2 = (HeaderBannerViewHolder) holder;
            vh2.setDataToView(mValues.get(position), vh2, position);
        }*/ else {
            ItemViewHolder vh3 = (ItemViewHolder) holder;
            vh3.setDataToView(mValues.get(position), vh3, position);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void onItemHolderClick(ItemViewHolder holder) {
        if (onItemClickListener != null)
            onItemClickListener.onItemClick(null, holder.itemView, holder.getAdapterPosition(), holder.getItemId());
    }

    @Override
    public int getItemViewType(int position) {
       /* if (position == 0) {
            return TYPE_HEADER;
        } else if (position == 1) {
            return TYPE_BANNER;
        } else {*/
            return TYPE_ITEM;
//        }

        //return (position == 0) ? TYPE_HEADER : TYPE_ITEM;
    }
}




