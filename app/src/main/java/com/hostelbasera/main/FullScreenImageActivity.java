package com.hostelbasera.main;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hostelbasera.R;
import com.hostelbasera.utility.BaseActivity;
import com.hostelbasera.utility.Constant;
import com.hostelbasera.utility.Globals;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FullScreenImageActivity extends BaseActivity {

    @BindView(R.id.vp_image_slider)
    ViewPager vpImageSlider;
    @BindView(R.id.rv_thumb_image)
    RecyclerView rvThumbImage;

    ImagePagerAdapter imagePagerAdapter;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.img_place_holder)
    ImageView imgPlaceHolder;
    @BindView(R.id.ll_viewPager)
    LinearLayout llViewPager;
    @BindView(R.id.tv_count)
    TextView tvCount;
    int size = 0;
    Globals globals;
    ArrayList<String> arrProductImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        ButterKnife.bind(this);
        globals = ((Globals) getApplicationContext());

        if (getIntent() != null) {
            arrProductImages =  getIntent().getStringArrayListExtra(Constant.ArrProductImages);

            size = arrProductImages.size();
            setImageCount((getIntent().getIntExtra(Constant.Position, 0) + 1) + " of " + size);
                /*rvThumbImage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                rvThumbImage.setItemAnimator(new DefaultItemAnimator());
                imageThumbAdapter = new ImageThumbAdapter(this, all_catalog_detail.products);
                rvThumbImage.setAdapter(imageThumbAdapter);
                imageThumbAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        for (int i = 0; i < all_catalog_detail.products.size(); i++) {
                            all_catalog_detail.products.get(i).isSelected = position == i;
                        }
                        imageThumbAdapter.notifyDataSetChanged();
                        vpImageSlider.setCurrentItem(position, true);
                    }
                });*/

            setVpImageSlider();

            vpImageSlider.setCurrentItem(getIntent().getIntExtra(Constant.Position, 0), true);


        } else {
            finish();
        }
    }

    public void setImageCount(String textCount) {
        tvCount.setText(textCount);
    }

    public void setVpImageSlider() {
        imagePagerAdapter = new ImagePagerAdapter(this, arrProductImages, true);
        vpImageSlider.setAdapter(imagePagerAdapter);

        vpImageSlider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                /*for (int i = 0; i < arrProductImages.size(); i++) {
                    all_catalog_detail.products.get(i).isSelected = position == i;
                }*/
//                imageThumbAdapter.notifyDataSetChanged();
//                rvThumbImage.smoothScrollToPosition(position);
                setImageCount((position + 1) + " of " + size);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R.id.img_close)
    public void onViewClicked() {
        onBackPressed();
    }
}
