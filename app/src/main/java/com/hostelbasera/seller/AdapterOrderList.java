package com.hostelbasera.seller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hostelbasera.R;
import com.hostelbasera.main.AdapterDocuments;
import com.hostelbasera.model.OrderListModel;
import com.hostelbasera.utility.Globals;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Chirag on 10/11/20.
 */
public class AdapterOrderList extends RecyclerView.Adapter<AdapterOrderList.ViewHolder> {

    private ArrayList<OrderListModel.Order_list> mValues;
    private final Context mContext;
    private AdapterView.OnItemClickListener onItemClickListener;
    Globals globals;
    public boolean isSeller;

    public AdapterOrderList(Context context) {
        mContext = context;
    }

    public void doRefresh(ArrayList<OrderListModel.Order_list> arrPropertyDetails, boolean isSeller) {
        mValues = arrPropertyDetails;
        globals = ((Globals) mContext.getApplicationContext());
        this.isSeller = isSeller;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdapterOrderList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_list, parent, false);
        return new AdapterOrderList.ViewHolder(view, this);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AdapterOrderList adapterOrderList;

        @BindView(R.id.tv_book_date)
        TextView tvBookDate;
        @BindView(R.id.tv_property_name)
        TextView tvPropertyName;
        @BindView(R.id.tv_room_name)
        TextView tvRoomName;
        @BindView(R.id.tv_lbl_booking_dates)
        TextView tvLblBookingDates;
        @BindView(R.id.tv_booking_dates)
        TextView tvBookingDates;
        @BindView(R.id.tv_user_name)
        TextView tvUserName;
        @BindView(R.id.ll_main)
        LinearLayout llMain;

        @BindView(R.id.tv_lbl_documents)
        TextView tvLblDocuments;
        @BindView(R.id.rv_document)
        RecyclerView rvDocument;

        @BindView(R.id.ll_rent)
        LinearLayout llRent;
        @BindView(R.id.tv_lbl_rent)
        TextView tvLblRent;
        @BindView(R.id.tv_rent)
        TextView tvRent;
        @BindView(R.id.ll_coupon)
        LinearLayout llCoupon;
        @BindView(R.id.tv_lbl_coupon)
        TextView tvLblCoupon;
        @BindView(R.id.tv_coupon)
        TextView tvCoupon;
        @BindView(R.id.vw_coupon)
        View vwCoupon;
        @BindView(R.id.ll_wallet)
        LinearLayout llWallet;
        @BindView(R.id.tv_lbl_wallet)
        TextView tvLblWallet;
        @BindView(R.id.tv_wallet)
        TextView tvWallet;
        @BindView(R.id.vw_wallet)
        View vwWallet;
        @BindView(R.id.tv_lbl_amount_payable)
        TextView tvLblAmountPayable;
        @BindView(R.id.tv_total_price)
        TextView tvTotalPrice;

        ViewHolder(View itemView, AdapterOrderList adapterOrderList) {
            super(itemView);
            this.adapterOrderList = adapterOrderList;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        void setDataToView(OrderListModel.Order_list mItem, AdapterOrderList.ViewHolder holder, int position) {

            Globals.doBoldTextView(tvLblBookingDates);
            Globals.doBoldTextView(tvPropertyName);
            Globals.doBoldTextView(tvLblDocuments);
            Globals.doBoldTextView(tvUserName);

            Globals.doBoldTextView(tvRent);
            Globals.doBoldTextView(tvCoupon);
            Globals.doBoldTextView(tvWallet);
            Globals.doBoldTextView(tvTotalPrice);

            tvPropertyName.setText(mItem.property_name);
            tvRoomName.setText("(" + Globals.checkString(mItem.room_name) + ")");

            tvBookDate.setText(globals.parseOrderDateToDDMMyyyy(mItem.created_at));
            tvBookingDates.setText(globals.parseDateToDDMMyyyy(mItem.start_date) + " - " + globals.parseDateToDDMMyyyy(mItem.end_date));

            tvUserName.setVisibility(isSeller ? View.VISIBLE : View.GONE);
            tvUserName.setText(Globals.checkString(mItem.user_name));

            llMain.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));

            tvRent.setText("₹ " + mItem.room_price);

            if (mItem.applied_wallet_amount > 0) {
                llWallet.setVisibility(View.VISIBLE);
                vwWallet.setVisibility(View.VISIBLE);
                tvWallet.setText("- ₹ " + mItem.applied_wallet_amount);
            } else {
                llWallet.setVisibility(View.GONE);
                vwWallet.setVisibility(View.GONE);
            }

            if (mItem.applied_coupon_amount > 0) {
                llCoupon.setVisibility(View.VISIBLE);
                vwCoupon.setVisibility(View.VISIBLE);
                tvCoupon.setText("₹ " + mItem.applied_coupon_amount);
            } else {
                llCoupon.setVisibility(View.GONE);
                vwCoupon.setVisibility(View.GONE);
            }

            tvTotalPrice.setText("₹ " + (mItem.room_price - mItem.applied_wallet_amount));

            if (mItem.user_documents != null && !mItem.user_documents.isEmpty()) {

                tvLblDocuments.setVisibility(View.VISIBLE);
                rvDocument.setVisibility(View.VISIBLE);

                AdapterDocuments adapterDocuments = new AdapterDocuments(mContext);

                rvDocument.setHasFixedSize(true);
                rvDocument.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                rvDocument.setAdapter(adapterDocuments);
                adapterDocuments.doRefresh(mItem.user_documents);
            } else {
                tvLblDocuments.setVisibility(View.GONE);
                rvDocument.setVisibility(View.GONE);
            }

        }

        @Override
        public void onClick(View v) {
            adapterOrderList.onItemHolderClick(AdapterOrderList.ViewHolder.this);
        }
    }

    @Override
    public void onBindViewHolder(final AdapterOrderList.ViewHolder holder, int position) {
        holder.setDataToView(mValues.get(position), holder, position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void onItemHolderClick(AdapterOrderList.ViewHolder holder) {
        if (onItemClickListener != null)
            onItemClickListener.onItemClick(null, holder.itemView, holder.getAdapterPosition(), holder.getItemId());
    }
}