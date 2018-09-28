package com.adpdigital.chabok.starter.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.adpdigital.chabok.starter.R;
import com.adpdigital.chabok.starter.application.StarterApp;
import com.adpdigital.push.AdpPushClient;
import com.adpdigital.push.AppState;
import com.adpdigital.push.Callback;
import com.adpdigital.push.ConnectionStatus;
import com.adpdigital.push.PushMessage;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Map;

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

        this.userIdTxt = findViewById(R.id.useridTextView);
        this.channelTxt = findViewById(R.id.channelTextView);

        this.messageBodyTxt = findViewById(R.id.messageBodyEditText);
        this.messgeUserIdTxt = findViewById(R.id.messageUseridTextView);
        this.messageChannelTxt = findViewById(R.id.messageChannelTextView);

        this.tagNameTxt = findViewById(R.id.tagsNameTextView);
        this.messageLogsTxt = findViewById(R.id.messageLogsTextView);

        final String chabokUserId = AdpPushClient.get().getUserId();
        if (chabokUserId != null) {
            this.userIdTxt.setText(chabokUserId);
        }

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onClick(View v) {
                String userId = MainActivity.this.userIdTxt.getText().toString();
                if (!userId.trim().contentEquals("")){
                    AdpPushClient.get().register(userId);
                } else {
                    Toast.makeText(getApplicationContext(),"UserId is empty. Please, enter a userId",Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button unregisterButton = findViewById(R.id.unregisterButton);
        unregisterButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onClick(View v) {
                AdpPushClient.get().unregister();
            }
        });

        Button subscribeButton = findViewById(R.id.subscribeButton);
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ShowToast")
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
        });

        Button unsubscribeButton = findViewById(R.id.subscribeButton);
        unsubscribeButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ShowToast")
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
        });


        Button publishMessageButton = findViewById(R.id.publishMessageButton);
        publishMessageButton.setOnClickListener(new View.OnClickListener() {
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
        });

        Button publishEventButton = findViewById(R.id.publishEventButton);
        publishEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = MainActivity.this.messgeUserIdTxt.getText().toString();
                if (!userId.isEmpty()){
                    String eventName = MainActivity.this.messageChannelTxt.getText().toString();
                    String msg = MainActivity.this.messageBodyTxt.getText().toString();

                    if (eventName.isEmpty()){
                        eventName = "sport";
                    }

                    if (msg.isEmpty()) {
                        msg = "Goal for Iran :)";
                    }

                    JSONObject data = new JSONObject();
                    try {
                        data.put("msg",msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    AdpPushClient.get().publishEvent(eventName,data);
                }
            }
        });

        Button addTagButton = findViewById(R.id.addTagButton);
        addTagButton.setOnClickListener(new View.OnClickListener() {
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
        });

        Button removeTagButton = findViewById(R.id.removeTagButton);
        removeTagButton.setOnClickListener(new View.OnClickListener() {
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
        });

        Button addToCartButton = findViewById(R.id.addToCartButton);
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject data = new JSONObject();
                try {
                    data.put("capId",123456);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                AdpPushClient.get().track("AddToCart",data);
            }
        });

        Button purchaseButton = findViewById(R.id.purchaseButton);
        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject data = new JSONObject();
                try {
                    data.put("capId",123456);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                AdpPushClient.get().track("Purchase",data);
            }
        });

        Button likeButton = findViewById(R.id.likeButton);
        likeButton.setOnClickListener(new View.OnClickListener() {
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
        });

        Button commentButton = findViewById(R.id.commentButton);
        commentButton.setOnClickListener(new View.OnClickListener() {
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
        });
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

    public void onEvent(final ConnectionStatus status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateConnectionStatus(status);
                Log.i(TAG, status.name());
            }
        });
    }

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
        TextView connectionStatus = (TextView) findViewById(R.id.connectionStatusTextView);
        View connectionStatusView =  findViewById(R.id.connectionStatusView);

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

}
