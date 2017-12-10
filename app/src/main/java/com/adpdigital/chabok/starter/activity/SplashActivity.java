package com.adpdigital.chabok.starter.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.adpdigital.chabok.starter.R;
import com.adpdigital.push.AdpPushClient;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(new Runnable() {

            public void run() {

                String userId = AdpPushClient.get().getUserId();

                Intent mainIntent = new Intent(SplashActivity.this, (userId != null && !"".equals(userId)) ? MainActivity.class : RegisterActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, 2000);
    }

}
