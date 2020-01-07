package com.hostelbasera.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.orhanobut.logger.Logger;

public class SmsBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsBroadcastReceiver";
    OtpReceivedInterface otpReceiveInterface = null;

    public void setOnOtpListeners(OtpReceivedInterface otpReceiveInterface) {
        this.otpReceiveInterface = otpReceiveInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d(TAG + " onReceive: ");
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status mStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            switch (mStatus.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    // Get SMS message contents'
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    Logger.d(TAG + " onReceive: failure " + message);
                    if (otpReceiveInterface != null) {
                        String otp = message.substring(message.indexOf(">") + 2, message.indexOf(" is"));
//                        <#> 669093 is the one-time password (OTP) for your login to the HostelBasera App. Please do not share with anyone /H6E5Z/7ag+
                        otpReceiveInterface.onOtpReceived(otp);
                    }
                    break;
                case CommonStatusCodes.TIMEOUT:
                    // Waiting for SMS timed out (5 minutes)
                    Logger.d(TAG + " onReceive: failure");
                    if (otpReceiveInterface != null) {
                        otpReceiveInterface.onOtpTimeout();
                    }
                    break;
            }
        }
    }
}
