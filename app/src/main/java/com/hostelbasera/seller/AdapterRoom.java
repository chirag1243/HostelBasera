package com.hostelbasera.seller;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hostelbasera.R;
import com.hostelbasera.model.AddRoomModel;
import com.hostelbasera.utility.Toaster;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterRoom extends RecyclerView.Adapter<AdapterRoom.ViewHolder> {

    public ArrayList<AddRoomModel> mValues = new ArrayList<>();
    private final Context mContext;
    private AdapterView.OnItemClickListener onItemClickListener;

    AdapterRoom(Context context) {
        mContext = context;
    }

    public void doAdd() {
        showRoomDialog(mValues.size(), true);
        notifyDataSetChanged();
    }

    public void doRefresh(ArrayList<AddRoomModel> arrAddRoomModels) {
        mValues = arrAddRoomModels;
        notifyDataSetChanged();
    }

    @Override
    public AdapterRoom.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rooms_item, parent, false);
        return new AdapterRoom.ViewHolder(view, this);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AdapterRoom adapterRoom;

        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.img_edit)
        ImageView imgEdit;
        @BindView(R.id.tv_remove)
        TextView tvRemove;

        ViewHolder(View itemView, AdapterRoom adapterRoom) {
            super(itemView);
            this.adapterRoom = adapterRoom;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void setDataToView(AddRoomModel mItem, AdapterRoom.ViewHolder holder, int position) {
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
                    showRoomDialog(position, false);
                }
            });
            holder.tvName.setText(mValues.get(position).name);
            holder.tvPrice.setText("₹ " + mValues.get(position).price);

        }

        @Override
        public void onClick(View v) {
            adapterRoom.onItemHolderClick(AdapterRoom.ViewHolder.this);
        }
    }

    private void showRoomDialog(int position, boolean isAdd) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView;
        if (inflater != null) {
            dialogView = inflater.inflate(R.layout.edit_room_dialog, null);

            dialogBuilder.setView(dialogView);
            final AlertDialog dialog = dialogBuilder.create();
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);

            EditText edtName = dialogView.findViewById(R.id.edt_name);
            EditText edtPrice = dialogView.findViewById(R.id.edt_price);
            Button btnSubmit = dialogView.findViewById(R.id.btn_submit);

            if (!isAdd) {
                edtName.setText(mValues.get(position).name);
                edtPrice.setText("" + mValues.get(position).price);
            }

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (edtName.getText().toString().trim().isEmpty()) {
                        Toaster.shortToast("Please enter name.");
                        return;
                    }
                    if (edtPrice.getText().toString().trim().isEmpty()) {
                        Toaster.shortToast("Please enter price.");
                        return;
                    }
                    try {
                        if (isAdd) {
                            AddRoomModel addRoomModel = new AddRoomModel();
                            addRoomModel.name = edtName.getText().toString().trim();
                            addRoomModel.price = Integer.parseInt(edtPrice.getText().toString().trim());

                            mValues.add(addRoomModel);
                        } else {
                            mValues.get(position).name = edtName.getText().toString().trim();
                            mValues.get(position).price = Integer.parseInt(edtPrice.getText().toString().trim());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    notifyDataSetChanged();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

    }


    @Override
    public void onBindViewHolder(final AdapterRoom.ViewHolder holder, int position) {
        holder.setDataToView(mValues.get(position), holder, position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void onItemHolderClick(AdapterRoom.ViewHolder holder) {
        if (onItemClickListener != null)
            onItemClickListener.onItemClick(null, holder.itemView, holder.getAdapterPosition(), holder.getItemId());
    }
}

