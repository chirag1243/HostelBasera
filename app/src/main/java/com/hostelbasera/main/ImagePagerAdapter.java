package com.hostelbasera.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.hostelbasera.R;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.Globals;

import java.util.ArrayList;

/**
 * Created by chirag
 */

public class ImagePagerAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private ArrayList<String> arrProducts;
    private boolean isZoomEnable;
    private long mLastClickTime = 0;
    Globals globals;

    public ImagePagerAdapter(Context context, ArrayList<String> arrayList, boolean isZoomEnable) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.arrProducts = arrayList;
        this.isZoomEnable = isZoomEnable;
        globals = ((Globals) context.getApplicationContext());
    }

    @Override
    public int getCount() {
        return arrProducts.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.prod_detail_image_item, container, false);
        ImageView imageView = itemView.findViewById(R.id.imageView);
        ImageView imgPlaceHolder = itemView.findViewById(R.id.img_place_holder);

        if (isZoomEnable) {
            try {
                imageView.setOnTouchListener(new ImageMatrixTouchHandler(container.getContext()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            imageView.setOnClickListener(v -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                mContext.startActivity(new Intent(mContext, FullScreenImageActivity.class)
                        .putExtra(Constant.ArrProductImages, arrProducts)
                        .putExtra(Constant.Position, position));

            });
        }

        /*Glide.with(mContext)
                .load(mContext.getString(R.string.image_server_url) + arrProducts.get(position).product_image1)
                .apply(new RequestOptions()
//                        .fitCenter()
//                        .centerCrop()
                        .placeholder(R.mipmap.ic_launcher)
                        .dontAnimate()
                        .priority(Priority.HIGH))
                .into(imageView);*/

        imgPlaceHolder.setVisibility(View.VISIBLE);


        Glide.with(mContext)
                .load(mContext.getString(R.string.image_url) + arrProducts.get(position)).apply(new RequestOptions().dontAnimate())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        imgPlaceHolder.setVisibility(View.GONE);
                        return false;
                    }
                }).into(imageView);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((FrameLayout) object);
    }
}

