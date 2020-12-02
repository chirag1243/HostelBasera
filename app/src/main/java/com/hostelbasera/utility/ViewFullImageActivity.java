package com.hostelbasera.utility;

import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.hostelbasera.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewFullImageActivity extends BaseActivity {

    @BindView(R.id.img_close)
    ImageView imgClose;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.img_place_holder)
    ImageView imgPlaceHolder;
    Globals globals;

    String file_name = "";
    boolean isFullPath = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_full_image);
        ButterKnife.bind(this);
        globals = ((Globals) getApplicationContext());

        if (getIntent() != null) {
            try {
                imageView.setOnTouchListener(new ImageMatrixTouchHandler(this));
            } catch (Exception e) {
                e.printStackTrace();
            }
            file_name = getIntent().getStringExtra(Constant.File_name);
            isFullPath = getIntent().getBooleanExtra(Constant.IsFullPath, false);
            Glide.with(this)
                    .load((isFullPath ? "" : getString(R.string.image_url)) + file_name).apply(new RequestOptions().dontAnimate())
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
        } else {
            finish();
        }
    }

    @OnClick(R.id.img_close)
    public void onViewClicked() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
