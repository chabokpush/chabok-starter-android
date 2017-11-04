package com.adpdigital.chabok.starter.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.adpdigital.chabok.starter.R;
import com.adpdigital.chabok.starter.common.Constants;
import com.adpdigital.push.AdpPushClient;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final EditText userId = (EditText) findViewById(R.id.user_id);
        Button registerBtn = (Button) findViewById(R.id.register_btn);


        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this);

                if(userId.getText() == null || userId.getText().toString().trim().equals("")){
                    userId.requestFocus();
                    userId.setError(getString(R.string.invalid_user_id));
                }else if ("".equals(myPref.getString(Constants.USER_ID, ""))) {

                    AdpPushClient client = AdpPushClient.get();
                    client.register(userId.getText().toString(), new String[]{Constants.CHANNEL_NAME});

                    SharedPreferences.Editor editor = myPref.edit();
                    editor.putString(Constants.USER_ID, userId.getText().toString());
                    editor.apply();

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                }

            }
        });
    }

}
