package com.hostelbasera.seller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hostelbasera.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterAddAttachment  extends RecyclerView.Adapter<AdapterAddAttachment.ViewHolder> {

    public ArrayList<String> mValues = new ArrayList<>();
    private final Context mContext;
    private AdapterView.OnItemClickListener onItemClickListener;

    AdapterAddAttachment(Context context) {
        mContext = context;
    }

    public void doAdd() {
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new ViewHolder(view, this);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AdapterAddAttachment adapterAddAttachment;

        @BindView(R.id.tv_contact)
        TextView tvContact;
        @BindView(R.id.img_edit)
        ImageView imgEdit;
        @BindView(R.id.tv_remove)
        TextView tvRemove;

        ViewHolder(View itemView, AdapterAddAttachment adapterAddAttachment) {
            super(itemView);
            this.adapterAddAttachment = adapterAddAttachment;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void setDataToView(String mItem, ViewHolder holder, int position) {
            tvRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mValues.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mValues.size());
                }
            });
            imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            holder.tvContact.setText(mValues.get(position));

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

