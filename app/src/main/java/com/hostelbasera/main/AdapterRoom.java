package com.hostelbasera.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hostelbasera.R;
import com.hostelbasera.model.GetPropertyDetailModel;
import com.hostelbasera.model.PropertyDetailModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterRoom extends RecyclerView.Adapter<AdapterRoom.ViewHolder> {

    private ArrayList<PropertyDetailModel.Propertyrooms> mValues;
    private final Context mContext;
    private AdapterView.OnItemClickListener onItemClickListener;
    public int room_id = 0;

    AdapterRoom(Context context) {
        mContext = context;
    }

    public void doRefresh(ArrayList<PropertyDetailModel.Propertyrooms> arrPropertyDetails) {
        mValues = arrPropertyDetails;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_detail_item, parent, false);
        return new ViewHolder(view, this);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AdapterRoom adapterRoom;

        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.radio)
        RadioButton radio;
        @BindView(R.id.cv_rooms)
        CardView cvRooms;

        ViewHolder(View itemView, AdapterRoom adapterRoom) {
            super(itemView);
            this.adapterRoom = adapterRoom;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        void setDataToView(PropertyDetailModel.Propertyrooms mItem, ViewHolder holder, int position) {
            tvName.setText("" + mItem.name);
            tvName.setTypeface(tvName.getTypeface(), Typeface.BOLD);
            tvPrice.setText("₹ " + mItem.price);
            tvPrice.setTypeface(tvPrice.getTypeface(), Typeface.BOLD);
            radio.setChecked(mItem.isSelected);
            cvRooms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    room_id = mItem.id;
                    doResetSelection();
                    mValues.get(position).isSelected = true;
                    notifyDataSetChanged();
                }
            });
        }

        void doResetSelection() {
            for (int i = 0; i < mValues.size(); i++) {
                mValues.get(i).isSelected = false;
            }
        }

        @Override
        public void onClick(View v) {
            adapterRoom.onItemHolderClick(ViewHolder.this);
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

