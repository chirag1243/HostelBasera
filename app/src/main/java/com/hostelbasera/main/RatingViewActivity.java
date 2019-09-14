package com.hostelbasera.main;

import android.os.Bundle;

import com.hostelbasera.R;
import com.hostelbasera.utility.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RatingViewActivity extends BaseActivity {

//    @BindView(R.id.gradientBackgroundView)
//    GradientBackgroundView gradientBackgroundView;
//    @BindView(R.id.emotionView)
//    EmotionView emotionView;
//    @BindView(R.id.ratingView)
//    RatingView ratingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_view);
        ButterKnife.bind(this);

//        ratingView.setRatingChangeListener(new Function2<Integer, Integer, Unit>() {
//            @Override
//            public Unit invoke(Integer previousRating, Integer newRating) {
//                emotionView.setRating(previousRating, newRating);
//                gradientBackgroundView.changeBackground(previousRating, newRating);
//                return null;
//            }
//        });

    }
}
