package com.adpdigital.chabok.starter.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.adpdigital.chabok.starter.R;
import com.adpdigital.chabok.starter.application.StarterApp;
import com.adpdigital.push.AdpPushClient;
import com.adpdigital.push.Callback;
import com.adpdigital.push.ConnectionStatus;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";
    private AdpPushClient chabok;
    private Process logcat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chabok = ((StarterApp) getApplication()).getPushClient();

        if (chabok.hasProtectedAppSupport()) {
            chabok.showProtectedAppSettings(MainActivity.this, getString(R.string.app_name), null, null);
        }

        final Button sendLog = (Button) findViewById(R.id.send_log);
        sendLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isExternalStorageWritable()) {
                    createAndShareLogFile();
                }

            }
        });

    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private void createAndShareLogFile() {


//        int permission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        String[] permissions = {
//                "android.permission.WRITE_EXTERNAL_STORAGE"
//        };
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            // We don't have permission so prompt the user
//            if (shouldAskPermissions()) {
//                ActivityCompat.requestPermissions(
//                        MainActivity.this,
//                        permissions,
//                        200
//                );
//            }
//
//        }

        File logFile = new File(getExternalFilesDir(null), "chaboklog.txt");

        try {

            boolean result = logFile.exists() || logFile.createNewFile();


            if (result) {

                String cmd = "logcat -c -d -f" + logFile.getAbsolutePath();
                Runtime.getRuntime().exec(cmd);

                shareFile(MainActivity.this, logFile.getAbsoluteFile());
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public boolean shouldAskPermissions() {
//        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
//    }


    private void shareFile(Context context, File file) {

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/*");
//        sharingIntent.setPackage("org.telegram.messenger");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
        context.startActivity(Intent.createChooser(sharingIntent, "share file with"));

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
