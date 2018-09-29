package com.adpdigital.chabok.starter.application;

import android.util.Log;
import android.app.Application;
import com.adpdigital.push.PushMessage;
import com.adpdigital.push.AdpPushClient;
import com.adpdigital.chabok.starter.activity.MainActivity;
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

        String userId = chabok.getUserId();
        if (userId != null && !userId.isEmpty()) {
            chabok.register(userId);
        }
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

            chabok.addListener(this);
        }
    }

    public void onEvent(PushMessage message){
        Log.d("MSG","Got Chabok message " + message);
    }

    @Override
    public void onTerminate() {
        if (chabok != null)
            chabok.dismiss();

        super.onTerminate();
    }
}
