package com.hostelbasera.utility;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Chirag
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        // init Calligraphy for support custom fonts
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}

