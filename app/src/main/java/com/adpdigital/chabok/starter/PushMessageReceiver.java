package com.adpdigital.chabok.starter;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.adpdigital.push.AdpPushClient;
import com.adpdigital.push.PushMessage;

public class PushMessageReceiver extends WakefulBroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle messageData = intent.getExtras();
        String channel = messageData.getString(AdpPushClient.PUSH_MSG_RECEIVED_TOPIC);
        String body = messageData.getString(AdpPushClient.PUSH_MSG_RECEIVED_MSG);
        PushMessage push = PushMessage.fromJson(body, channel);
        handleNewMessage(push);
        completeWakefulIntent(intent);
    }


    private void handleNewMessage(PushMessage message) {
        // write your code to handle new message
    }
}
