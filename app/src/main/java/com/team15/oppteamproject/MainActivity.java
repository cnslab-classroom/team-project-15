package com.team15.oppteamproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView onoffText;
    LinearLayout mainView;
    ImageView onoffBtn, contactBtn, messageBtn, settingBtn;
    private boolean isActive; // 활성화 상태를 나타내는 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 뷰 초기화
        onoffBtn = findViewById(R.id.onOffBtn);
        onoffText = findViewById(R.id.onOffText);
        contactBtn = findViewById(R.id.contactBtn);
        messageBtn = findViewById(R.id.messageBtn);
        settingBtn = findViewById(R.id.settingBtn);
        mainView = findViewById(R.id.mainView);

        // SharedPreferences에서 상태 복원
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        isActive = prefs.getBoolean("isActive", true); // 저장된 값이 없으면 기본값 true

        // 복원된 상태에 따라 UI 설정
        updateUI();

        // 연락처 버튼 클릭 이벤트
        contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ContactActivity.class));
                finish();
            }
        });

        messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MessageActivity.class));
                finish();
            }
        });

        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                finish();
            }
        });

        // 활성화/비활성화 버튼 클릭 이벤트
        onoffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 활성화 상태 토글
                isActive = !isActive;

                // UI 업데이트
                updateUI();

                // 상태 저장
                saveState();
            }
        });


    }

    // UI 업데이트 메서드
    private void updateUI() {
        if (isActive) { // true면 활성화로 바꿈
            onoffBtn.setImageResource(R.drawable.on_btn);
            onoffText.setText("활성화");
            mainView.setBackgroundColor(Color.parseColor("#D79911"));
        } else {
            onoffBtn.setImageResource(R.drawable.off_btn);
            onoffText.setText("비활성화");
            mainView.setBackgroundColor(Color.parseColor("#ACACAC"));
        }
    }

    // 상태 저장 메서드
    private void saveState() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isActive", isActive); // 현재 상태 저장
        editor.apply(); // 비동기적으로 저장
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 액티비티 종료 전에 상태 저장
        saveState();
    }
}