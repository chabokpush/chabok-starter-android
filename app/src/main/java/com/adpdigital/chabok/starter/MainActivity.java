package com.adpdigital.chabok.starter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.adpdigital.push.AdpPushClient;
import com.adpdigital.push.ConnectionStatus;

public class MainActivity extends AppCompatActivity {


    private AdpPushClient chabok;
    private TextView connectionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chabok = ((StarterApp) getApplication()).getPushClient();
        connectionStatus = (TextView) findViewById(R.id.connection_status);

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
            chabok.setPushListener(this);
        }
    }

    private void detachPushClient() {
        if (chabok != null) {
            chabok.removePushListener(this);
        }
    }

    public void onEvent(final ConnectionStatus status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateConnectionStatus(status);
            }
        });
    }

    private void updateConnectionStatus(ConnectionStatus status) {

        if (connectionStatus != null && status != null) {
            switch (status) {
                case CONNECTED:
                    connectionStatus.setText(getString(R.string.connected));
                    connectionStatus.setBackgroundResource(R.drawable.green_circle);
                    break;

                case CONNECTING:
                    connectionStatus.setText(getString(R.string.connecting));
                    connectionStatus.setBackgroundResource(R.drawable.orange_circle);
                    break;

                case DISCONNECTED:
                    connectionStatus.setText(getString(R.string.disconnected));
                    connectionStatus.setBackgroundResource(R.drawable.red_circle);
                    break;
            }
        }
    }

}
