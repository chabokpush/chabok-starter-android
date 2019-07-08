package com.adpdigital.chabok.starter.activity;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import org.json.JSONObject;
import android.widget.Toast;
import android.widget.Button;
import org.json.JSONException;
import android.widget.EditText;
import android.widget.TextView;
import com.adpdigital.push.AppState;
import com.adpdigital.push.Callback;
import android.annotation.SuppressLint;

import com.adpdigital.push.ChabokEvent;
import com.adpdigital.push.PushMessage;
import com.adpdigital.chabok.starter.R;
import com.adpdigital.push.AdpPushClient;
import com.adpdigital.push.ConnectionStatus;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";
    private AdpPushClient chabok;
    private EditText userIdTxt;
    private EditText channelTxt;

    private EditText messageBodyTxt;
    private EditText messgeUserIdTxt;
    private EditText messageChannelTxt;

    private EditText tagNameTxt;
    private TextView messageLogsTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chabok = AdpPushClient.get();
        chabok.addListener(this);

        if (chabok.hasProtectedAppSupport()) {
            chabok.showProtectedAppSettings(MainActivity.this, getString(R.string.app_name), null, null);
        }

        this.userIdTxt = (EditText) findViewById(R.id.useridTextView);
        this.channelTxt = (EditText) findViewById(R.id.channelTextView);

        this.messageBodyTxt = (EditText) findViewById(R.id.messageBodyEditText);
        this.messgeUserIdTxt = (EditText) findViewById(R.id.messageUseridTextView);
        this.messageChannelTxt = (EditText) findViewById(R.id.messageChannelTextView);

        this.tagNameTxt = (EditText) findViewById(R.id.tagsNameTextView);
        this.messageLogsTxt = (TextView) findViewById(R.id.messageLogsTextView);

        final String chabokUserId = AdpPushClient.get().getUserId();
        if (chabokUserId != null) {
            this.userIdTxt.setText(chabokUserId);
        }

        // Register to chabok
        Button registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this.registerBtnOnClick());

        Button unregisterButton = (Button) findViewById(R.id.unregisterButton);
        unregisterButton.setOnClickListener(this.unregisterBtnOnClick());

        Button subscribeButton = (Button) findViewById(R.id.subscribeButton);
        subscribeButton.setOnClickListener(this.subscribeBtnOnClick());

        Button unsubscribeButton = (Button) findViewById(R.id.unsubscribeButton);
        unsubscribeButton.setOnClickListener(this.unsubscribeBtnOnClick());

        // Publish
        Button publishEventButton = (Button) findViewById(R.id.publishEventButton);
        publishEventButton.setOnClickListener(this.publishEventBtnOnClick());

        Button publishMessageButton = (Button) findViewById(R.id.publishMessageButton);
        publishMessageButton.setOnClickListener(this.publishMessageBtnOnClick());

        // Tag
        Button addTagButton = (Button) findViewById(R.id.addTagButton);
        addTagButton.setOnClickListener(this.addTagBtnOnClick());

        Button removeTagButton = (Button) findViewById(R.id.removeTagButton);
        removeTagButton.setOnClickListener(this.removeTagBtnOnClick());

        // Track
        Button addToCartButton = (Button) findViewById(R.id.addToCartButton);
        addToCartButton.setOnClickListener(this.addToCartBtnOnClick());

        Button purchaseButton = (Button) findViewById(R.id.purchaseButton);
        purchaseButton.setOnClickListener(this.purchaseBtnOnClick());

        Button likeButton = (Button) findViewById(R.id.likeButton);
        likeButton.setOnClickListener(this.likeBtnOnClick());

        Button commentButton = (Button) findViewById(R.id.commentButton);
        commentButton.setOnClickListener(this.commentBtnOnClick());

        Button setUserAttributeButton = (Button) findViewById(R.id.setUserAttributeButton);
        setUserAttributeButton.setOnClickListener(this.setUserAttributeButtonOnClick());

        Button incrementUserAttributeButton = (Button) findViewById(R.id.incrementUserAttributeButton);
        incrementUserAttributeButton.setOnClickListener(this.incrementUserAttributeButtonOnClick());

//        Button publishBackgroundButton = (Button) findViewById(R.id.publishBackgroundButton);
//        publishBackgroundButton.setOnClickListener(this.publishBackgroundButtonOnClick());

        Intent intent = getIntent();
        AdpPushClient.get().appWillOpenUrl(intent.getData());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Uri data = intent.getData();
        AdpPushClient.get().appWillOpenUrl(data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachPushClient();
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachPushClient();
    }

    @Override
    protected void onDestroy() {
        detachPushClient();
        super.onDestroy();
    }

    private void attachPushClient() {
        if (chabok != null) {
            chabok.addListener(this);
        }

        fetchAndUpdateConnectionStatus();
    }

    private void detachPushClient() {
        if (chabok != null) {
            chabok.removePushListener(this);
        }
    }

    @SuppressLint("SetTextI18n")
    public void onEvent(final PushMessage message){
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                MainActivity.this.messageLogsTxt.setText(
                        MainActivity.this.messageLogsTxt.getText() +
                                "\n " + message + "\n\n---------------------");
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void onEvent(final ConnectionStatus status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateConnectionStatus(status);
                Log.i(TAG, status.name());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void onEvent(AppState state){
        switch (state) {
            case REGISTERED:
                Log.d(TAG, "Registered ..........");
                break;
            case INSTALL:
                Log.d(TAG, "Install ..........");
                break;
            case LAUNCH:
                Log.d(TAG, "Launch ..........");
                break;
            default:
                Log.d(TAG, "Protected grant needed ..........");
        }
    }

    private void fetchAndUpdateConnectionStatus() {
        if (chabok == null) {
            return;
        }
        chabok.getStatus(new Callback<ConnectionStatus>() {
            @Override
            public void onSuccess(ConnectionStatus connectionStatus) {
                Log.i(TAG + "_fetch", connectionStatus.name());
                updateConnectionStatus(connectionStatus);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.i(TAG, "errrror ");
            }
        });
    }

    private void updateConnectionStatus(ConnectionStatus status) {
        View connectionStatusView =  findViewById(R.id.connectionStatusView);
        TextView connectionStatus = (TextView) findViewById(R.id.connectionStatusTextView);

        if (connectionStatus != null && status != null) {
            switch (status) {
                case CONNECTED:
                    connectionStatus.setText(getString(R.string.connected));
                    connectionStatusView.setBackgroundResource(R.drawable.green_circle);
                    break;

                case CONNECTING:
                    connectionStatus.setText(getString(R.string.connecting));
                    connectionStatusView.setBackgroundResource(R.drawable.orange_circle);
                    break;

                case DISCONNECTED:
                    connectionStatus.setText(getString(R.string.disconnected));
                    connectionStatusView.setBackgroundResource(R.drawable.red_circle);
                    break;
            }
        }
    }

    //------------ Register to chabok
    private View.OnClickListener registerBtnOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = MainActivity.this.userIdTxt.getText().toString();
                if (!userId.trim().contentEquals("")){
                    AdpPushClient.get().register(userId);
                } else {
                    Toast.makeText(getApplicationContext(),"UserId is empty. Please, enter a userId",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private View.OnClickListener unregisterBtnOnClick(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdpPushClient.get().unregister();
            }
        };
    }

    private View.OnClickListener subscribeBtnOnClick(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String channel = MainActivity.this.channelTxt.getText().toString();
                if (!channel.isEmpty()){
                    AdpPushClient.get().subscribe(channel, new Callback() {
                        @Override
                        public void onSuccess(Object o) {
                            Toast.makeText(getApplicationContext(),
                                    "Subscribed on channel " + channel,
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Toast.makeText(getApplicationContext(),
                                    "Fail to subscribe on channel for reason " + throwable.getLocalizedMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(),"Channel is empty. Please, enter a channel name to subscribe on it",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private View.OnClickListener unsubscribeBtnOnClick(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String channel = MainActivity.this.channelTxt.getText().toString();
                if (!channel.isEmpty()){
                    AdpPushClient.get().unsubscribe(channel, new Callback() {
                        @Override
                        public void onSuccess(Object o) {
                            Toast.makeText(getApplicationContext(),
                                    "Unsubscribe to channel " + channel,
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Toast.makeText(getApplicationContext(),
                                    "Fail to subscribe on channel for reason " + throwable.getLocalizedMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(),"Channel is empty. Please, enter a channel name to unsubscribe to it",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    // ---------------- Publish
    private View.OnClickListener publishMessageBtnOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = MainActivity.this.messgeUserIdTxt.getText().toString();
                if (!userId.isEmpty()){
                    String channel = MainActivity.this.messageChannelTxt.getText().toString();
                    String messageBody = MainActivity.this.messageBodyTxt.getText().toString();

                    if (channel.isEmpty()){
                        channel = "default";
                    }

                    if (messageBody.isEmpty()) {
                        messageBody = "Hello world :)";
                    }

                    AdpPushClient.get().publish(userId, channel, messageBody, new Callback() {
                        @Override
                        public void onSuccess(Object o) {
                            Toast.makeText(getApplicationContext(),"Message was successfully sent", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Toast.makeText(getApplicationContext(),"Fail to send message", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        };
    }

    private View.OnClickListener publishEventBtnOnClick() {
        return new View.OnClickListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onClick(View v) {
                String eventName = MainActivity.this.messageChannelTxt.getText().toString();
                String msg = MainActivity.this.messageBodyTxt.getText().toString();

                if (!eventName.isEmpty()) {
                    if (msg.isEmpty()) {
                        msg = "Goal for Iran :)";
                    }

                    JSONObject data = new JSONObject();
                    try {
                        data.put("msg", msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    AdpPushClient.get().publishEvent(eventName, data);
                } else {
                    Toast.makeText(getApplicationContext(),"Event name is empty.", Toast.LENGTH_SHORT);
                }
            }
        };
    }

    // ---------- Tags
    private View.OnClickListener addTagBtnOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tagName = MainActivity.this.tagNameTxt.getText().toString();
                if (!tagName.isEmpty()){
                    AdpPushClient.get().addTag(tagName, new Callback() {
                        @Override
                        public void onSuccess(Object o) {
                            Toast.makeText(getApplicationContext(), "Tag added to userId devices", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Toast.makeText(getApplicationContext(), "Fail adding tag to userId devices", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(),"Tag name is empty",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private View.OnClickListener removeTagBtnOnClick() {
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String tagName = MainActivity.this.tagNameTxt.getText().toString();
                if (!tagName.isEmpty()){
                    AdpPushClient.get().removeTag(tagName, new Callback() {
                        @Override
                        public void onSuccess(Object o) {
                            Toast.makeText(getApplicationContext(), "Tag removed to userId devices", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Toast.makeText(getApplicationContext(), "Fail adding tag to userId devices", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(),"Tag name is empty",Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    // ------------ Track
    private View.OnClickListener addToCartBtnOnClick() {
        return new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                JSONObject data = new JSONObject();
                try {
                    data.put("capId",123456);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ChabokEvent event = new ChabokEvent(50000, "RIAL");
                event.setData(data);

                AdpPushClient.get().trackPurchase("AddToCard", event);
            }
        };
    }

    private View.OnClickListener purchaseBtnOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject data = new JSONObject();
                try {
                    data.put("capId",123456);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                AdpPushClient.get().trackPurchase("Purchase", new ChabokEvent(20000, "RIAL"));
            }
        };
    }

    private View.OnClickListener likeBtnOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject data = new JSONObject();
                try {
                    data.put("postId",654321);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                AdpPushClient.get().track("Like",data);
            }
        };
    }

    private View.OnClickListener commentBtnOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject data = new JSONObject();
                try {
                    data.put("postId",8654321);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                AdpPushClient.get().track("Comment",data);
            }
        };
    }

    private View.OnClickListener setUserAttributeButtonOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> attribute = new HashMap<>();
                attribute.put("firstName", "Chabok");
                attribute.put("lastName", "Platform");
                attribute.put("age", 5);
                attribute.put("gender", "Male");
                attribute.put("shoesSize", 43);


                AdpPushClient.get().setUserAttributes(attribute);
            }
        };
    }

    private View.OnClickListener incrementUserAttributeButtonOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdpPushClient.get().incrementUserAttribute("comedy_movie", 1);
            }
        };
    }

//    private View.OnClickListener publishBackgroundButtonOnClick() {
//        return new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                JSONObject data = new JSONObject();
//
//                try {
//                    data.put("lat", 32.3);
//                    data.put("lng", 52.4);
//                    data.put("ts", System.currentTimeMillis());
//                    AdpPushClient.get().publishBackground("geo", data);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        };
//    }
}
