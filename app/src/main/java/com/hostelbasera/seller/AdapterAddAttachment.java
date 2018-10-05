package com.hostelbasera.seller;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
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
import com.hostelbasera.model.AddImageAttachmentModel;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.ViewFullImageActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterAddAttachment extends RecyclerView.Adapter<AdapterAddAttachment.ViewHolder> {

    public ArrayList<AddImageAttachmentModel> mValues = new ArrayList<>();
    private final Context mContext;
    private AdapterView.OnItemClickListener onItemClickListener;

    AdapterAddAttachment(Context context) {
        mContext = context;
    }

    public void doRefresh(ArrayList<AddImageAttachmentModel> arrAddImageAttachment) {
        mValues = arrAddImageAttachment;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attachment_item, parent, false);
        return new ViewHolder(view, this);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AdapterAddAttachment adapterAddAttachment;

        @BindView(R.id.img_attachment_icon)
        ImageView imgAttachmentIcon;
        @BindView(R.id.tv_remove)
        TextView tvRemove;

        ViewHolder(View itemView, AdapterAddAttachment adapterAddAttachment) {
            super(itemView);
            this.adapterAddAttachment = adapterAddAttachment;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void setDataToView(AddImageAttachmentModel mItem, ViewHolder holder, int position) {
            tvRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mValues.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mValues.size());
                }
            });

            Glide.with(mContext)
                    .load(mItem.FilePath)
                    .apply(new RequestOptions()
                            .fitCenter()
                            .placeholder(R.drawable.image_placeholder)
                            .dontAnimate()
                            .priority(Priority.HIGH))
                    .into(imgAttachmentIcon);
            imgAttachmentIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, ViewFullImageActivity.class).putExtra(Constant.File_name, mItem.FilePath).putExtra(Constant.IsFullPath, true));
                }
            });

        }

        @Override
        public void onClick(View v) {
            adapterAddAttachment.onItemHolderClick(ViewHolder.this);
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

