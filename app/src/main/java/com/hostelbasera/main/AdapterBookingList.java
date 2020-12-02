package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hostelbasera.R;
import com.hostelbasera.model.BookingListDataModel;
import com.hostelbasera.utility.Globals;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Chirag on 29/10/20.
 */
public class AdapterBookingList extends RecyclerView.Adapter<AdapterBookingList.ViewHolder> {

    private ArrayList<BookingListDataModel.Booking_requests> mValues;
    private final Context mContext;
    private AdapterView.OnItemClickListener onItemClickListener;
    Globals globals;
    public boolean isSeller;
    OnBookingStatusListener mListener;

    public interface OnBookingStatusListener {
        void onChangeStatus(int id, int status);

        void onMakePayment(int id, int seller_id, int property_id, int price);
    }

    public AdapterBookingList(Context context) {
        mContext = context;
    }

    public void doRefresh(ArrayList<BookingListDataModel.Booking_requests> arrPropertyDetails, boolean isSeller, OnBookingStatusListener listener) {
        mValues = arrPropertyDetails;
        globals = ((Globals) mContext.getApplicationContext());
        this.isSeller = isSeller;
        mListener = listener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdapterBookingList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_list_item, parent, false);
        return new AdapterBookingList.ViewHolder(view, this);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AdapterBookingList adapterBookingList;

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
        @BindView(R.id.btn_status)
        Button btnStatus;
        @BindView(R.id.btn_confirm)
        Button btnConfirm;
        @BindView(R.id.tv_user_name)
        TextView tvUserName;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.ll_main)
        LinearLayout llMain;

        @BindView(R.id.tv_lbl_documents)
        TextView tvLblDocuments;
        @BindView(R.id.rv_document)
        RecyclerView rvDocument;


        ViewHolder(View itemView, AdapterBookingList adapterBookingList) {
            super(itemView);
            this.adapterBookingList = adapterBookingList;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        void setDataToView(BookingListDataModel.Booking_requests mItem, ViewHolder holder, int position) {

            Globals.doBoldTextView(tvLblBookingDates);
            Globals.doBoldTextView(tvPropertyName);
            Globals.doBoldTextView(tvPrice);
            Globals.doBoldTextView(tvLblDocuments);

            tvPropertyName.setText(mItem.property_name);
            tvRoomName.setText("(" + Globals.checkString(mItem.room_name) + ")");

            tvBookDate.setText(globals.parseOrderDateToDDMMyyyy(mItem.created_at));
            tvBookingDates.setText(globals.parseDateToDDMMyyyy(mItem.start_date) + " - " + globals.parseDateToDDMMyyyy(mItem.end_date));

            tvUserName.setVisibility(isSeller ? View.VISIBLE : View.GONE);
            tvUserName.setText(Globals.checkString(mItem.user_name));

            tvPrice.setText("₹ " + Globals.checkString(mItem.room_price));

            String status = "";
            llMain.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));

            if (isSeller) {

                switch (mItem.status) {
                    case 0://Pending
                        status = "Cancel";
                        btnStatus.setEnabled(true);
                        btnStatus.setVisibility(View.VISIBLE);
                        btnConfirm.setVisibility(View.VISIBLE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            setBackgroundColor(R.color.high_check);
                        }
                        break;
                    case 1://Accept
                        btnStatus.setVisibility(View.VISIBLE);
                        btnConfirm.setVisibility(View.GONE);

                        status = "Confirmed";
                        btnStatus.setEnabled(false);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            setBackgroundColor(R.color.colorPrimary);
                        }
                        break;
                    case 2://Cancel
                        btnStatus.setVisibility(View.VISIBLE);
                        btnConfirm.setVisibility(View.GONE);

                        status = "Cancelled";
                        btnStatus.setEnabled(false);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            setBackgroundColor(R.color.high_check);
                        }
                        llMain.setBackgroundColor(ContextCompat.getColor(mContext, R.color.bg_gray));
                        break;
                }
            } else {
                switch (mItem.status) {
                    case 0://Pending
                        status = "Pending";
                        btnStatus.setEnabled(false);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            setBackgroundColor(R.color.border_gray);
                        }
                        break;
                    case 1://Accept
                        status = "Pay Now";
                        btnStatus.setEnabled(true);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            setBackgroundColor(R.color.colorPrimary);
                        }
                        break;
                    case 2://Cancel
                        status = "Cancel";
                        btnStatus.setEnabled(false);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            setBackgroundColor(R.color.high_check);
                        }
                        break;
                }
            }

            btnStatus.setText(status);

            btnStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItem.status == 1 && !isSeller) {
                        //int id, int seller_id, int property_id, String price
                        mListener.onMakePayment(mItem.id, mItem.seller_id, mItem.property_id, Globals.checkInteger(mItem.room_price));
                    } else {
                        mListener.onChangeStatus(mItem.id, 2);
                    }
                }
            });

//            btnConfirm.setVisibility(isSeller ? View.VISIBLE : View.GONE);


            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onChangeStatus(mItem.id, 1);
                }
            });

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

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        private void setBackgroundColor(int color) {
            btnStatus.setBackgroundTintList(mContext.getResources().getColorStateList(color));
        }


        @Override
        public void onClick(View v) {
            adapterBookingList.onItemHolderClick(AdapterBookingList.ViewHolder.this);
        }
    }

    @Override
    public void onBindViewHolder(final AdapterBookingList.ViewHolder holder, int position) {
        holder.setDataToView(mValues.get(position), holder, position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void onItemHolderClick(AdapterBookingList.ViewHolder holder) {
        if (onItemClickListener != null)
            onItemClickListener.onItemClick(null, holder.itemView, holder.getAdapterPosition(), holder.getItemId());
    }
}