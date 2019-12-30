package com.hostelbasera.utility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.multidex.MultiDex;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hostelbasera.R;
import com.hostelbasera.model.UserDetailModel;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import io.fabric.sdk.android.Fabric;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Random;

public class Globals extends CoreApp {
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Logger.addLogAdapter(new AndroidLogAdapter());
        context = getApplicationContext();
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    public SharedPreferences.Editor getEditor() {
        return editor = (editor == null) ? getSharedPref().edit() : editor;
    }

    public SharedPreferences getSharedPref() {
        return sp = (sp == null) ? getSharedPreferences(Constant.AS_Secrets, Context.MODE_PRIVATE) : sp;
    }

    public void clearAllSharedPreferences() {
        SharedPreferences sharedPref = getSharedPref();
        sharedPref.edit().clear().apply();
    }

    /**
     * check if network is available or not
     *
     * @param context
     * @return true or false
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = new Network[0];
            if (connectivityManager != null) {
                networks = connectivityManager.getAllNetworks();
            }
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                if (connectivityManager != null) {
                    networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                    if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                        return true;
                    }
                }

            }
        } else {
            if (connectivityManager != null) {
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean internetCheck(Context context, boolean showDialog) {
        if (isNetworkAvailable(context))
            return true;
        if (showDialog) {
            showAlertDialog(context, context.getString(R.string.msg_NO_INTERNET_TITLE), context.getString(R.string.msg_NO_INTERNET_MSG), false);
        }
        return false;
    }

    public static void showAlertDialog(final Context context, String pTitle, final String pMsg, Boolean status) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle(pTitle);
            builder.setMessage(pMsg);
            builder.setCancelable(true);
            builder.setPositiveButton(context.getString(R.string.msg_goto_settings),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                            context.startActivity(intent);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean validateEmailAddress(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static void hideKeyboard(Activity activity) {
        try {
            if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
                }
            }

            InputMethodManager inputManager = null;
            if (activity != null) {
                inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputManager != null) {
                    inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }

        } catch (Exception e) {
            Log.e("Globals", "Exception : Try to close keyboard that is already not visible");
        }
    }


    public static String getMobile(String myString) {
        if (myString.length() > 10)
            return myString.substring(myString.length() - 10);
        else
            return myString;
    }

    public String parseDateToMMMddyyyy(String datetime) {

        String inputPattern = "yyyy-MM-dd"; // 2017-25-10
        String outputPattern = "MMMM dd, yyyy"; // April 10, 2018
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.ENGLISH);
        String str = "";
        try {
            str = outputFormat.format(inputFormat.parse(datetime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public String parseOrderDateToEddMMMMyyyy(String datetime) {

        String inputPattern = "yyyy-MM-dd"; //2018-04-30
        String outputPattern = "E, dd MMM, yyyy"; // Thu, 17 May, 2018
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.ENGLISH);
        String str = "";
        try {
            str = outputFormat.format(inputFormat.parse(datetime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public String parseOrderDateToDD(String datetime) {

        String inputPattern = "yyyy-MM-dd"; //2018-04-30
        String outputPattern = "dd"; // 17
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.ENGLISH);
        String str = "";
        try {
            str = outputFormat.format(inputFormat.parse(datetime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public String parseOrderDateToMMMyyyy(String datetime) {

        String inputPattern = "yyyy-MM-dd"; //2018-04-30
        String outputPattern = "MMM yyyy"; // May 2018
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.ENGLISH);
        String str = "";
        try {
            str = outputFormat.format(inputFormat.parse(datetime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public String parseOrderDateToddMMMMyyyy(String datetime) {

        String inputPattern = "yyyy-MM-dd"; //2018-04-30
        String outputPattern = "dd-MMMM-yyyy"; // 17-March-2018
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.ENGLISH);
        String str = "";
        try {
            str = outputFormat.format(inputFormat.parse(datetime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    @SuppressLint("WorldReadableFiles")
    public String readFromFile(Context context, String fileName) {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets().open(fileName, Context.MODE_WORLD_READABLE);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line = "";
            while ((line = input.readLine()) != null) {
                line.replace("\\", "");
                returnString.append(line);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fIn != null)
                    fIn.close();
                if (input != null)
                    input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }
        return returnString.toString();
    }

    /*UserDetails*/
    public UserDetailModel getUserDetails() {
        return toUserDetails(getSharedPref().getString(Constant.USER_MODEL, null));
    }

    public void setUserDetails(UserDetailModel userMap) {
        getEditor().putString(Constant.USER_MODEL, toJsonString(userMap));
        getEditor().commit();
    }

    public static UserDetailModel toUserDetails(String params) {
        if (params == null)
            return null;

        return new Gson().fromJson(params, new TypeToken<UserDetailModel>() {
        }.getType());
    }

    public static String toJsonString(UserDetailModel params) {
        if (params == null) {
            return null;
        }
        return new Gson().toJson(params, new TypeToken<UserDetailModel>() {
        }.getType());
    }

    public void setNewUserId(int userId) {
        getEditor().putInt(Constant.User_id, userId);
        getEditor().commit();
    }

    public int getNewUserId() {
        return getSharedPref().getInt(Constant.User_id, 0);
    }

    public void setIsSeller(boolean isSeller) {
        getEditor().putBoolean(Constant.IsSeller, isSeller);
        getEditor().commit();
    }

    public boolean getIsSeller() {
        return getSharedPref().getBoolean(Constant.IsSeller, false);
    }

    public void setFCMDeviceToken(String regId) {
        getEditor().putString(Constant.Fcm_token, regId);
        getEditor().commit();
    }

    public void setCityId(int cityId) {
        getEditor().putInt(Constant.City_id, cityId);
        getEditor().commit();
    }

    public int getCityId() {
        return getSharedPref().getInt(Constant.City_id, 0);
    }


    public String getFCMDeviceToken() {
        return getSharedPref().getString(Constant.Fcm_token, "");
    }


    public static void darkenStatusBar(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(darkenColor(ContextCompat.getColor(activity, color)));
        }
    }

    // Code to darken the color supplied (mostly color of toolbar)
    public static int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }


    public static int getViewHeight(View view) {
        WindowManager wm = (WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int deviceWidth;

        Point size = new Point();
        display.getSize(size);
        deviceWidth = size.x;

        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(deviceWidth, View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        return view.getMeasuredHeight(); //        view.getMeasuredWidth();
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static String getFileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }

    public static boolean checkFileSize(String path) {

// Get length of file in bytes
        double fileSizeInBytes = new File(path).length();
// Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        double fileSizeInKB = fileSizeInBytes / 1024;
// Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        double fileSizeInMB = fileSizeInKB / 1024;
        return fileSizeInMB > Constant.MAX_FILE_SIZE;
    }

    public static int randomNumber(){
        final int min = 10;
        final int max = 10000;
        return new Random().nextInt((max - min) + 1) + min;
    }
}
