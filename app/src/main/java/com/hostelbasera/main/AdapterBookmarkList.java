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
import com.google.gson.Gson;
import com.hostelbasera.R;
import com.hostelbasera.apis.HttpRequestHandler;
import com.hostelbasera.apis.PostRequest;
import com.hostelbasera.model.BookmarkDetailModel;
import com.hostelbasera.model.PropertyDetailModel;
import com.hostelbasera.utility.Toaster;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterBookmarkList extends RecyclerView.Adapter<AdapterBookmarkList.ViewHolder> {

    private ArrayList<BookmarkDetailModel.BookmarkDetails> mValues;
    private final Context mContext;
    private AdapterView.OnItemClickListener onItemClickListener;

    AdapterBookmarkList(Context context) {
        mContext = context;
    }

    public void doRefresh(ArrayList<BookmarkDetailModel.BookmarkDetails> arrBookmarkDetails) {
        mValues = arrBookmarkDetails;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item, parent, false);
        return new ViewHolder(view, this);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AdapterBookmarkList adapterBookmarkList;

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
        @BindView(R.id.vw_bottom_border)
        View vwBottomBorder;
        @BindView(R.id.img_delete)
        ImageView imgDelete;

        ViewHolder(View itemView, AdapterBookmarkList adapterBookmarkList) {
            super(itemView);
            this.adapterBookmarkList = adapterBookmarkList;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        void setDataToView(BookmarkDetailModel.BookmarkDetails mItem, ViewHolder holder, int position) {

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

            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRemoveBookmarkClicked(position);
                }
            });

        }

        @Override
        public void onClick(View v) {
            adapterBookmarkList.onItemHolderClick(ViewHolder.this);
        }
    }

    private void onRemoveBookmarkClicked(int position) {
        JSONObject postData = HttpRequestHandler.getInstance().getAddBookmarkParam(mValues.get(position).property_id, true);

        if (postData != null) {

            new PostRequest(mContext, mContext.getString(R.string.addBookmark), postData, true, new PostRequest.OnPostServiceCallListener() {
                @Override
                public void onSucceedToPostCall(JSONObject response) {
                    PropertyDetailModel propertyDetailModel = new Gson().fromJson(response.toString(), PropertyDetailModel.class);
                    if (propertyDetailModel.status == 0) {
                        mValues.remove(position);
                        notifyDataSetChanged();
                    }
                    Toaster.shortToast(propertyDetailModel.message);
                }

                @Override
                public void onFailedToPostCall(int statusCode, String msg) {
                    Toaster.shortToast(msg);
                }
            }).execute();
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
