package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.hostelbasera.R;
import com.hostelbasera.model.PropertyDetailModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterReview extends RecyclerView.Adapter<AdapterReview.ViewHolder> {

    private ArrayList<PropertyDetailModel.PropertyReviewDetails> mValues;
    private Context mContext;
    private AdapterView.OnItemClickListener onItemClickListener;

    AdapterReview(Context context, ArrayList<PropertyDetailModel.PropertyReviewDetails> arrPropertyReviewDetails) {
        mContext = context;
        mValues = arrPropertyReviewDetails;
    }

    public void doRefresh() {
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        if (mValues.size() > 1) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = (int) (parent.getWidth() * 0.9);
            view.setLayoutParams(layoutParams);
        }
        return new ViewHolder(view, this);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AdapterReview adapterReview;
        @BindView(R.id.tv_user_name)
        TextView tvUserName;
        @BindView(R.id.tv_rating)
        TextView tvRating;
        @BindView(R.id.tv_review)
        TextView tvReview;
        @BindView(R.id.tv_review_date)
        TextView tvReviewDate;

        public ViewHolder(View itemView, AdapterReview adapterReview) {
            super(itemView);
            this.adapterReview = adapterReview;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        void setDataToView(PropertyDetailModel.PropertyReviewDetails mItem, ViewHolder holder, int position) {
            tvUserName.setTypeface(tvUserName.getTypeface(), Typeface.BOLD);
            tvRating.setTypeface(tvRating.getTypeface(), Typeface.BOLD);
            tvUserName.setText(mItem.user_name);
            tvRating.setText(mItem.rating + " / 5");
            tvReview.setText(mItem.review);
            tvReviewDate.setText(mItem.date);
        }

        @Override
        public void onClick(View v) {
            adapterReview.onItemHolderClick(ViewHolder.this);
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


