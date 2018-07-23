package com.hostelbasera.utility;

import android.content.Context;
import android.util.AttributeSet;

import com.hostelbasera.R;

/**
 * Created by Chirag.
 * For Calligraphy.
 */

public class TextField extends android.support.v7.widget.AppCompatTextView {

    public TextField(final Context context, final AttributeSet attrs) {
        super(context, attrs, R.attr.textFieldStyle);
    }
}
