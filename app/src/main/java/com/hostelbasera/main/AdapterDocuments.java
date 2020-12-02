package com.hostelbasera.main;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.hostelbasera.R;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.ViewFullImageActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Chirag on 07/11/20.
 */
public class AdapterDocuments extends RecyclerView.Adapter<AdapterDocuments.ViewHolder> {

    public ArrayList<String> mValues = new ArrayList<>();
    private final Context mContext;
    private AdapterView.OnItemClickListener onItemClickListener;

    public AdapterDocuments(Context context) {
        mContext = context;
    }

    public void doRefresh(ArrayList<String> arrayList) {
        mValues = arrayList;
        notifyDataSetChanged();
    }

    @Override
    public AdapterDocuments.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attachment_item, parent, false);
        return new AdapterDocuments.ViewHolder(view, this);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AdapterDocuments adapterDocuments;

        @BindView(R.id.img_attachment_icon)
        ImageView imgAttachmentIcon;
        @BindView(R.id.tv_remove)
        TextView tvRemove;

        ViewHolder(View itemView, AdapterDocuments adapterDocuments) {
            super(itemView);
            this.adapterDocuments = adapterDocuments;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void setDataToView(String mItem, AdapterDocuments.ViewHolder holder, int position) {
            tvRemove.setVisibility(View.GONE);

            Glide.with(mContext)
                    .load(mItem)
                    .apply(new RequestOptions()
                            .fitCenter()
                            .placeholder(R.drawable.image_placeholder)
                            .dontAnimate()
                            .priority(Priority.HIGH))
                    .into(imgAttachmentIcon);
            imgAttachmentIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, ViewFullImageActivity.class).putExtra(Constant.File_name, mItem).putExtra(Constant.IsFullPath, true));
                }
            });

        }

        @Override
        public void onClick(View v) {
            adapterDocuments.onItemHolderClick(AdapterDocuments.ViewHolder.this);
        }
    }


    @Override
    public void onBindViewHolder(final AdapterDocuments.ViewHolder holder, int position) {
        holder.setDataToView(mValues.get(position), holder, position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void onItemHolderClick(AdapterDocuments.ViewHolder holder) {
        if (onItemClickListener != null)
            onItemClickListener.onItemClick(null, holder.itemView, holder.getAdapterPosition(), holder.getItemId());
    }
}

