package com.hostelbasera.utility;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import androidx.annotation.IntDef;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.hostelbasera.R;

/**
 * Created by Chirag.
 */

public class Toaster {

    @IntDef({Toast.LENGTH_LONG, Toast.LENGTH_SHORT})
    private @interface ToastLength {
    }

    public static void shortToast(View view, Window window, @StringRes int text) {
        Toast toast = makeToast(CoreApp.getInstance().getString(text), Toast.LENGTH_SHORT);

        Rect rect = new Rect();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        int[] viewLocation = new int[2];
        view.getLocationInWindow(viewLocation);
        int viewLeft = viewLocation[0] - rect.left;
        int viewTop = viewLocation[1] - rect.top;

        DisplayMetrics metrics = new DisplayMetrics();
        window.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(metrics.widthPixels, View.MeasureSpec.UNSPECIFIED);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(metrics.heightPixels, View.MeasureSpec.UNSPECIFIED);
        toast.getView().measure(widthMeasureSpec, heightMeasureSpec);

        int toastY = viewTop + view.getHeight();

        toast.setGravity(Gravity.TOP | Gravity.CENTER_VERTICAL, 0, toastY);
        toast.show();
    }

    public static void shortToast(@StringRes int text) {
        shortToast(CoreApp.getInstance().getString(text));
    }

    public static void shortToast(String text) {
        show(text, Toast.LENGTH_SHORT);
    }

    public static void longToast(@StringRes int text) {
        longToast(CoreApp.getInstance().getString(text));
    }

    public static void longToast(String text) {
        show(text, Toast.LENGTH_LONG);
    }

    @SuppressLint("ShowToast")
    private static Toast makeToast(String text, @ToastLength int length) {
        Toast toast = Toast.makeText(CoreApp.getInstance(), text, Toast.LENGTH_SHORT);
        View view = toast.getView();

        //Gets the actual oval background of the Toast then sets the colour filter
        view.getBackground().setColorFilter(ContextCompat.getColor(CoreApp.getInstance(), R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        //Gets the TextView from the Toast so it can be editted
        TextView textView = view.findViewById(android.R.id.message);
        textView.setTextColor(ContextCompat.getColor(CoreApp.getInstance(), R.color.white));
        return toast;//Toast.makeText(CoreApp.getInstance(), text, length);
    }

    private static void show(String text, @ToastLength int length) {
        makeToast(text, length).show();
    }


}
