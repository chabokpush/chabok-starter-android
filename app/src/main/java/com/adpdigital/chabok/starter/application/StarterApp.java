package com.adpdigital.chabok.starter.application;

import android.net.Uri;
import android.util.Log;
import android.os.Bundle;
import org.json.JSONObject;
import android.app.Application;

import androidx.core.app.NotificationCompat;

import com.adpdigital.push.ChabokNotificationAction;
import com.adpdigital.push.OnDeeplinkResponseListener;
import com.adpdigital.push.PushMessage;
import com.adpdigital.push.AdpPushClient;
import com.adpdigital.push.ChabokNotification;
import com.adpdigital.push.NotificationHandler;
import com.adpdigital.chabok.starter.activity.MainActivity;
import static com.adpdigital.chabok.starter.common.Constants.YOUR_APP_ID;
import static com.adpdigital.chabok.starter.common.Constants.SDK_PASSWORD;
import static com.adpdigital.chabok.starter.common.Constants.SDK_USERNAME;
import static com.adpdigital.chabok.starter.common.Constants.YOUR_API_KEY;

public class StarterApp extends Application {

    private final String TAG = this.getClass().getName();
    private AdpPushClient chabok = null;

    @Override
    public void onCreate() {
        super.onCreate();

        initPushClient();

        String userId = chabok.getUserId();
        if (userId != null && !userId.isEmpty()) {
            chabok.register(userId);
        } else {
            chabok.registerAsGuest();
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
            chabok.addNotificationHandler(getNotificationHandler());
            chabok.setDefaultTracker("8iFRmA");

            chabok.setOnDeeplinkResponseListener(new OnDeeplinkResponseListener() {
                @Override
                public boolean launchReceivedDeeplink(Uri uri) {
                    return true;
                }
            });
        }
    }

    private NotificationHandler getNotificationHandler(){
        return new NotificationHandler(){

            @Override
            public Class getActivityClass(ChabokNotification chabokNotification) {
                // return preferred activity class to be opened on this message's notification
                return MainActivity.class;
            }

            @Override
            public boolean buildNotification(ChabokNotification chabokNotification, NotificationCompat.Builder builder) {
                // use builder to customize the notification object
                // return false to prevent this notification to be shown to the user, otherwise true
                getDataFromChabokNotification(chabokNotification);
                return true;
            }

            @Override
            public boolean notificationOpened(ChabokNotification message, ChabokNotificationAction notificationAction) {
                if (notificationAction.type == ChabokNotificationAction.ActionType.ActionTaken){
                    //Click on an action.
                } else if (notificationAction.type == ChabokNotificationAction.ActionType.Opened){
                    //Notification opened
                } else if (notificationAction.type == ChabokNotificationAction.ActionType.Dismissed){
                    //Notification dismissed
                }

                //true to prevent launch activity that returned from getActivityClass or navigation to a url.
                return super.notificationOpened(message, notificationAction);
            }
        };
    }

    private void getDataFromChabokNotification(ChabokNotification chabokNotification) {
        if (chabokNotification != null) {
            if (chabokNotification.getExtras() != null) {
                Bundle payload = chabokNotification.getExtras();

                //FCM message data is here
                Object data = payload.get("data");
                if (data != null) {
                    Log.d(TAG, "getDataFromChabokNotification: The ChabokNotification data is : " + String.valueOf(data));
                }
            } else if (chabokNotification.getMessage() != null) {
                PushMessage payload = chabokNotification.getMessage();

                //Chabok message data is here
                JSONObject data = payload.getData();
                if (data != null) {
                    Log.d(TAG, "getDataFromChabokNotification: The ChabokNotification data is : " + data);
                }
            }
        }
    }

    public void onEvent(PushMessage message) {
        Log.d(TAG, "\n\n--------------------\n\nGOT MESSAGE " + message + "\n\n");
        JSONObject data = message.getData();
        if (data != null){
            Log.d(TAG, "--------------------\n\n The message data is : " + data + "\n\n");
        }
    }

    @Override
    public void onTerminate() {
        if (chabok != null)
            chabok.dismiss();

        super.onTerminate();
    }
}
