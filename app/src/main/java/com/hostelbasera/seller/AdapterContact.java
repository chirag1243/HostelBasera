package com.hostelbasera.seller;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hostelbasera.R;
import com.hostelbasera.utility.Toaster;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterContact extends RecyclerView.Adapter<AdapterContact.ViewHolder> {

    public ArrayList<String> mValues = new ArrayList<>();
    private final Context mContext;
    private AdapterView.OnItemClickListener onItemClickListener;

    AdapterContact(Context context) {
        mContext = context;
    }

    public void doAdd() {
        showContactDialog(mValues.size(), true);
        notifyDataSetChanged();
    }

    public void doRefresh(ArrayList<String> arrContact) {
        mValues = arrContact;
        notifyDataSetChanged();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new ViewHolder(view, this);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AdapterContact adapterContact;

        @BindView(R.id.tv_contact)
        TextView tvContact;
        @BindView(R.id.img_edit)
        ImageView imgEdit;
        @BindView(R.id.tv_remove)
        TextView tvRemove;

        ViewHolder(View itemView, AdapterContact adapterContact) {
            super(itemView);
            this.adapterContact = adapterContact;
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
                    showContactDialog(position, false);
                }
            });
            holder.tvContact.setText(mValues.get(position));
        }

        @Override
        public void onClick(View v) {
            adapterContact.onItemHolderClick(ViewHolder.this);
        }
    }

    private void showContactDialog(int position, boolean isAdd) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView;
        if (inflater != null) {
            dialogView = inflater.inflate(R.layout.edit_contact_dialog, null);

            dialogBuilder.setView(dialogView);
            final AlertDialog dialog = dialogBuilder.create();
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);

            EditText edtContact = dialogView.findViewById(R.id.edt_contact);
            Button btnSubmit = dialogView.findViewById(R.id.btn_submit);

            if (!isAdd)
                edtContact.setText(mValues.get(position));

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (edtContact.getText().toString().trim().isEmpty()) {
                        Toaster.shortToast("Please enter contact.");
                        return;
                    }
                    if (isAdd) {
                        mValues.add(edtContact.getText().toString().trim());
                    } else {
                        mValues.set(position, edtContact.getText().toString().trim());
                    }
                    notifyDataSetChanged();
                    dialog.dismiss();
                }
            });
            dialog.show();
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

