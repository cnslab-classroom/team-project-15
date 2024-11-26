package com.team15.oppteamproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity {

    ImageView onoffBtn, contactBtn, messageBtn, settingBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onoffBtn = findViewById(R.id.onOffBtn);
        contactBtn = findViewById(R.id.contactBtn);
        messageBtn = findViewById(R.id.messageBtn);
        settingBtn = findViewById(R.id.settingBtn);

        contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ContactActivity.class));
                finish();
            }
        });



    }
}