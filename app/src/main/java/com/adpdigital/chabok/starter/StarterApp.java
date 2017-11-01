package com.adpdigital.chabok.starter;

import android.app.Application;

import com.adpdigital.push.AdpPushClient;


public class StarterApp extends Application {

    // TODO: use your data from your account in panel

    private static final String YOUR_APP_ID = "YOUR_APP_ID/SENDER_ID";
    private static final String YOUR_API_KEY = "YOUR_API_KEY";
    private static final String SDK_USERNAME = "SDK_USERNAME";
    private static final String SDK_PASSWORD = "SDK_PASSWORD";

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

            // TODO: use your logic for unique user id

            chabok.register("USER_ID");
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
