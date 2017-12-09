package com.adpdigital.chabok.starter.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.adpdigital.chabok.starter.R;
import com.adpdigital.chabok.starter.application.StarterApp;
import com.adpdigital.chabok.starter.common.DeviceUtil;
import com.adpdigital.push.AdpPushClient;
import com.adpdigital.push.Callback;
import com.adpdigital.push.ConnectionStatus;

public class MainActivity extends AppCompatActivity {

    private static final String FIRST_RUN_PREFERENCES_NAME = "skipProtectedAppsMessage";

    String TAG = "MainActivity";
    private AdpPushClient chabok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chabok = ((StarterApp) getApplication()).getPushClient();

        // if deviceManufacturer is huawei or xiaomi, show protected apps guide
        String deviceManufacturer = DeviceUtil.getDeviceManufacturer().toLowerCase();
        if(!TextUtils.isEmpty(deviceManufacturer) && (deviceManufacturer.contains("huawei") ||
                deviceManufacturer.contains("xiaomi"))) {
            showGuide(deviceManufacturer);
        }
    }

    private void showGuide(final String deviceManufacturer) {

        final SharedPreferences preferences = getSharedPreferences("ProtectedApps", Context.MODE_PRIVATE);
        boolean isFirstTime = preferences.getBoolean(FIRST_RUN_PREFERENCES_NAME, false);
        if (!isFirstTime) {
            showProtectedAppDialog(deviceManufacturer);
        }
    }

    private void showProtectedAppDialog(String deviceManufacturer) {
        DeviceUtil.ifHuaweiAlert(this, deviceManufacturer);
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

    public void onEvent(final ConnectionStatus status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateConnectionStatus(status);
                Log.i(TAG, status.name());
            }
        });
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

        TextView connectionStatus = (TextView) findViewById(R.id.connection_status);

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
