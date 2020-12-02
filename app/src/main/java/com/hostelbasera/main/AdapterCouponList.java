package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.hostelbasera.R;
import com.hostelbasera.model.CouponsModel;
import com.hostelbasera.utility.Globals;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Chirag on 05/11/20.
 */
public class AdapterCouponList extends RecyclerView.Adapter<AdapterCouponList.ViewHolder> {

    private ArrayList<CouponsModel.Coupons> mValues;
    private final Context mContext;
    private AdapterView.OnItemClickListener onItemClickListener;
    Globals globals;
    OnCouponApplyListener mListener;

    public interface OnCouponApplyListener {
        void onCouponApply(int amount_type, int amount, String code, int id);
    }

    public AdapterCouponList(Context context) {
        mContext = context;
    }

    public void doRefresh(ArrayList<CouponsModel.Coupons> arrPropertyDetails, OnCouponApplyListener listener) {
        mValues = arrPropertyDetails;
        globals = ((Globals) mContext.getApplicationContext());
        mListener = listener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdapterCouponList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_list_item, parent, false);
        return new AdapterCouponList.ViewHolder(view, this);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AdapterCouponList adapterCouponList;

        @BindView(R.id.tv_expiry_date)
        TextView tvExpiryDate;
        @BindView(R.id.tv_coupon)
        TextView tvCoupon;
        @BindView(R.id.tv_amount)
        TextView tvAmount;
        @BindView(R.id.btn_apply)
        Button btnApply;

        ViewHolder(View itemView, AdapterCouponList adapterCouponList) {
            super(itemView);
            this.adapterCouponList = adapterCouponList;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        void setDataToView(CouponsModel.Coupons mItem, AdapterCouponList.ViewHolder holder, int position) {
            Globals.doBoldTextView(tvCoupon);
            Globals.doBoldTextView(tvAmount);

            tvCoupon.setText(mItem.code);

            tvExpiryDate.setText(mContext.getString(R.string.expiry_date) + " " + globals.parseOrderDateToDDMMyyyy(mItem.expiry_date));

            tvAmount.setText(mItem.amount_type == 0 ? mItem.amount + "% " : "₹ " + mItem.amount);

            btnApply.setText(mItem.isApplied ? mContext.getString(R.string.applied) : mContext.getString(R.string.apply));

            btnApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mItem.isApplied) {
                        mListener.onCouponApply(mItem.amount_type, mItem.amount, mItem.code, mItem.id);
                        doResetApply(position);
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            adapterCouponList.onItemHolderClick(AdapterCouponList.ViewHolder.this);
        }

        public void doResetApply(int position) {
            for (int i = 0; i < mValues.size(); i++) {
                mValues.get(i).isApplied = position == i;
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public void onBindViewHolder(final AdapterCouponList.ViewHolder holder, int position) {
        holder.setDataToView(mValues.get(position), holder, position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void onItemHolderClick(AdapterCouponList.ViewHolder holder) {
        if (onItemClickListener != null)
            onItemClickListener.onItemClick(null, holder.itemView, holder.getAdapterPosition(), holder.getItemId());
    }
}