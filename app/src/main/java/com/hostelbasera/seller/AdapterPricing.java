package com.hostelbasera.seller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.hostelbasera.R;
import com.hostelbasera.model.CheckSellerPaymentDataModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterPricing extends RecyclerView.Adapter<AdapterPricing.ViewHolder> {

    private ArrayList<CheckSellerPaymentDataModel.PriceBlockDetails> mValues;
    private final Context mContext;
    private AdapterView.OnItemClickListener onItemClickListener;


    AdapterPricing(Context context) {
        mContext = context;
    }

    public void doRefresh(ArrayList<CheckSellerPaymentDataModel.PriceBlockDetails> arrBookmarkDetails) {
        mValues = arrBookmarkDetails;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pricing_item, parent, false);
        return new ViewHolder(view, this);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AdapterPricing adapterPricing;

        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.tv_description)
        TextView tvDescription;
        @BindView(R.id.btn_buy_now)
        TextView btnBuyNow;

        ViewHolder(View itemView, AdapterPricing adapterPricing) {
            super(itemView);
            this.adapterPricing = adapterPricing;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        void setDataToView(CheckSellerPaymentDataModel.PriceBlockDetails mItem, ViewHolder holder, int position) {

            tvTitle.setTypeface(tvTitle.getTypeface(), Typeface.BOLD);
            tvPrice.setTypeface(tvPrice.getTypeface(), Typeface.BOLD);
            tvDescription.setTypeface(tvDescription.getTypeface(), Typeface.BOLD);
            btnBuyNow.setTypeface(btnBuyNow.getTypeface(), Typeface.BOLD);

            tvTitle.setText("" + mItem.title);
            tvDescription.setText("" + mItem.discription);
            tvPrice.setText("₹ " + mItem.price);

//            btnBuyNow.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    onRemoveBookmarkClicked(position);
//                    Toaster.shortToast("Buy Now");
//                }
//            });

        }

        @Override
        public void onClick(View v) {
            adapterPricing.onItemHolderClick(ViewHolder.this);
        }
    }

    /*private void onRemoveBookmarkClicked(int position) {
        JSONObject postData = HttpRequestHandler.getInstance().getAddBookmarkParam(mValues.get(position).propertyId, true);

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
    }*/

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
