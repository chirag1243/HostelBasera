package com.hostelbasera.main;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.hostelbasera.R;
import com.hostelbasera.model.BannerListModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Chirag on 11/11/20.
 */
public class AdapterBanners extends RecyclerView.Adapter<AdapterBanners.ViewHolder> {

    public ArrayList<BannerListModel.Banners> mValues = new ArrayList<>();
    private final Context mContext;
    private AdapterView.OnItemClickListener onItemClickListener;

    public AdapterBanners(Context context) {
        mContext = context;
    }

    public void doRefresh(ArrayList<BannerListModel.Banners> arrayList) {
        mValues = arrayList;
        notifyDataSetChanged();
    }

    @Override
    public AdapterBanners.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_item, parent, false);
        if (mValues.size() > 1) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//            layoutParams.width = (int) (parent.getWidth() * 0.9);
            view.setLayoutParams(layoutParams);
        }
        return new AdapterBanners.ViewHolder(view, this);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AdapterBanners adapterBanners;

        @BindView(R.id.imageView)
        ImageView imageView;

        ViewHolder(View itemView, AdapterBanners adapterBanners) {
            super(itemView);
            this.adapterBanners = adapterBanners;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void setDataToView(BannerListModel.Banners mItem, AdapterBanners.ViewHolder holder, int position) {
            Glide.with(mContext)
                    .load(mContext.getString(R.string.banner_url) + mItem.image)
                    .apply(new RequestOptions()
                            .fitCenter()
                            .dontAnimate()
                            .priority(Priority.HIGH))
                    .into(imageView);
            //.placeholder(R.drawable.place_holder_small)
        }

        @Override
        public void onClick(View v) {
            adapterBanners.onItemHolderClick(AdapterBanners.ViewHolder.this);
        }
    }


    @Override
    public void onBindViewHolder(final AdapterBanners.ViewHolder holder, int position) {
        holder.setDataToView(mValues.get(position), holder, position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void onItemHolderClick(AdapterBanners.ViewHolder holder) {
        if (onItemClickListener != null)
            onItemClickListener.onItemClick(null, holder.itemView, holder.getAdapterPosition(), holder.getItemId());
    }
}


