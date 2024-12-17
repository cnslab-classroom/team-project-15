package com.team15.oppteamproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static IncidentHandler incidentHandler;
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 101;
    private static final int REQUEST_CODE_FINE_LOCATION = 102;

    TextView onoffText;
    LinearLayout mainView;
    ImageView onoffBtn, contactBtn, messageBtn, settingBtn;
    private boolean isActive; // 활성화 상태를 나타내는 변수

    public static IncidentHandler getIncidentHandler() {
        return incidentHandler;
    }

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



        // IncidentHandler 초기화
        TextView accelerometerTextView = findViewById(R.id.tv_accelerometer);
        incidentHandler = new IncidentHandler(this, accelerometerTextView);

        Button testbtn = (Button) findViewById(R.id.testbtn);
        testbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incidentHandler.setCrashDetected();
            }
        });



        // SharedPreferences에서 상태 복원
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        isActive = prefs.getBoolean("isActive", true);

        // 복원된 상태에 따라 UI 설정
        updateUI();

        // 권한 확인
        checkNotificationPermission();
        checkLocationPermission();

        // 버튼 클릭 이벤트
        contactBtn.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, ContactActivity.class));
            finish();
        });

        messageBtn.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, MessageActivity.class));
            finish();
        });

        settingBtn.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
            finish();
        });

        onoffBtn.setOnClickListener(view -> {
            isActive = !isActive;
            if (isActive) {
                incidentHandler.startMonitoring(); // 센서 모니터링 시작
            } else {
                incidentHandler.stopMonitoring(); // 센서 모니터링 중단
            }
            updateUI();
            saveState();
        });
    }

    // UI 업데이트 메서드
    public void updateUI() {
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
        editor.putBoolean("isActive", isActive);
        editor.apply();
    }

    // 알림 권한 확인 및 요청
    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_POST_NOTIFICATIONS);
            }
        }
    }

    // 위치 권한 확인 및 요청
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_FINE_LOCATION);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState(); // 액티비티 종료 전에 상태 저장
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "알림 권한이 허용되었습니다.");
            } else {
                Log.e("MainActivity", "알림 권한이 거부되었습니다.");
            }
        } else if (requestCode == REQUEST_CODE_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "위치 권한이 허용되었습니다.");
            } else {
                Log.e("MainActivity", "위치 권한이 거부되었습니다.");
            }
        }
    }
}
