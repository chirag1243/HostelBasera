package com.hostelbasera.seller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.hostelbasera.R;
import com.hostelbasera.model.SellerPropertyModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterContact extends RecyclerView.Adapter<AdapterContact.ViewHolder> {

    private ArrayList<String> mValues = new ArrayList<>();
    private final Context mContext;
    private AdapterView.OnItemClickListener onItemClickListener;

    AdapterContact(Context context) {
        mContext = context;
    }

    public void doAdd() {
        mValues.add("");
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new ViewHolder(view, this);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AdapterContact adapterContact;

        @BindView(R.id.edt_contact)
        EditText edtContact;
        @BindView(R.id.tv_remove)
        TextView tvRemove;

        ViewHolder(View itemView, AdapterContact adapterContact) {
            super(itemView);
            this.adapterContact = adapterContact;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        void setDataToView(String mItem, ViewHolder holder, int position) {

            tvRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mValues.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mValues.size());
                }
            });
            edtContact.setText(mItem);

            edtContact.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                public void afterTextChanged(Editable editable) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (edtContact.getTag() != null) {
                        mValues.set((int) edtContact.getTag(), charSequence.toString());
                    }
                }
            });

        }

        @Override
        public void onClick(View v) {
            adapterContact.onItemHolderClick(ViewHolder.this);
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

