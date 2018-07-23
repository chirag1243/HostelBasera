package com.hostelbasera.utility;

import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;

import com.hostelbasera.BuildConfig;
import com.hostelbasera.R;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;


import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class CoreApp extends MultiDexApplication {
    private static CoreApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        new Runnable() {
            @Override
            public void run() {
                Logger.addLogAdapter(new AndroidLogAdapter() {
                    @Override
                    public boolean isLoggable(int priority, String tag) {
                        return BuildConfig.DEBUG;
                    }
                });

                //Your code
                CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath(Constant.AS_DEAFAULT_FONT)
                        .setFontAttrId(R.attr.fontPath)
                        .addCustomViewWithSetTypeface(CustomViewWithTypefaceSupport.class)
                        .addCustomStyle(TextField.class, R.attr.textFieldStyle)
                        .build()
                );
            }
        }.run();


        /*if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        enabledStrictMode();
        LeakCanary.install(this);*/
    }

    public static synchronized CoreApp getInstance() {
        return instance;
    }

    private static void enabledStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .build());
    }

}


