package com.adpdigital.chabok.starter.application;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.adpdigital.chabok.starter.activity.MainActivity;
import com.adpdigital.chabok.starter.common.Constants;
import com.adpdigital.push.AdpPushClient;

import static com.adpdigital.chabok.starter.common.Constants.SDK_PASSWORD;
import static com.adpdigital.chabok.starter.common.Constants.SDK_USERNAME;
import static com.adpdigital.chabok.starter.common.Constants.YOUR_API_KEY;
import static com.adpdigital.chabok.starter.common.Constants.YOUR_APP_ID;


public class StarterApp extends Application {

    private AdpPushClient chabok = null;

    @Override
    public void onCreate() {
        super.onCreate();
        initPushClient();
    }

    private synchronized void initPushClient() {
        if (chabok == null) {
            chabok = AdpPushClient.init(
                    getApplicationContext(),
                    MainActivity.class,
                    YOUR_APP_ID,
                    YOUR_API_KEY,
                    SDK_USERNAME,
                    SDK_PASSWORD
            );
            chabok.setDevelopment(true);

            SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String userId = myPref.getString(Constants.USER_ID, "");

            if (!"".equals(userId)) {
                chabok.reRegister(userId);
            }
        }
    }

    public synchronized AdpPushClient getPushClient() throws IllegalStateException {
        if (chabok == null) {
            throw new IllegalStateException("Adp Push Client not initialized");
        }
        return chabok;
    }

    @Override
    public void onTerminate() {
        if (chabok != null)
            chabok.dismiss();

        super.onTerminate();
    }
}
