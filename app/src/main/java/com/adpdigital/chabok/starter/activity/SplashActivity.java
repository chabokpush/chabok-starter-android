package com.adpdigital.chabok.starter.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.adpdigital.chabok.starter.R;
import com.adpdigital.chabok.starter.common.Constants;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(this);


        new Handler().postDelayed(new Runnable() {

            public void run() {

                String userId = myPref.getString(Constants.USER_ID, "");

                Intent mainIntent = new Intent(SplashActivity.this, (!"".equals(userId)) ? MainActivity.class : RegisterActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, 2000);
    }

}
