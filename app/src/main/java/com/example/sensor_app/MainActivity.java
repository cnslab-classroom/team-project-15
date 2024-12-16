package com.example.sensor_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;

    private TextView tvAccelerometer, tvGyroscope;

    // 충돌 및 의식 상실 감지 변수
    private boolean crashDetected = false;
    private boolean unconsciousDetected = false;
    private boolean checkingUnconscious = false;
    private long unconsciousStartTime = 0L; // 10.5 이하 상태가 시작된 시간 기록용
    //    private static final float CRASH_THRESHOLD = 83.9f;
    private static final float CRASH_THRESHOLD = 15f;
    private static final float UNCONSCIOUS_THRESHOLD = 10.5f;
    private static final long UNCONSCIOUS_DURATION = 10_000L; // 10초(10000ms)

    // 푸시 알림 상수
    private static final String CHANNEL_ID = "channel_id_example"; // 채널 ID
    private static final String CHANNEL_NAME = "Example Channel"; // 채널 이름
    private static final String CHANNEL_DESCRIPTION = "Notification Channel for Example"; // 채널 설명
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 1; // 권한 요청 코드

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 채널 생성 (Android 8.0 이상)
        createNotificationChannel();

        tvAccelerometer = findViewById(R.id.tv_accelerometer);
        tvGyroscope = findViewById(R.id.tv_gyroscope);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if (accelerometer == null) {
            tvAccelerometer.setText("가속도 센서를 지원하지 않습니다.");
        }
        if (gyroscope == null) {
            tvGyroscope.setText("자이로 센서를 지원하지 않습니다.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Root값 계산
            double rootValue = Math.sqrt(x*x + y*y + z*z);

            // 충돌 감지 로직
            if (!crashDetected && rootValue > CRASH_THRESHOLD) {
                crashDetected = true;
                // 여기서 crashDetected를 1로 표현하려면 별도의 int 변수 사용 가능
                // int crashStatus = 1;
            }

            // 충돌 감지 이후 의식 상실 감지 로직
            if (crashDetected && !unconsciousDetected) {
                if (rootValue <= UNCONSCIOUS_THRESHOLD) {
                    checkAndroidVersion();
                    // 만약 아직 체크 시작 전이면 시작 시간 기록
                    if (!checkingUnconscious) {
                        checkingUnconscious = true;
                        unconsciousStartTime = System.currentTimeMillis();
                    } else {
                        // 이미 체크 중이라면 현재 시간이 시작 시간으로부터 10초 이상 지났는지 확인
                        long elapsed = System.currentTimeMillis() - unconsciousStartTime;
                        if (elapsed >= UNCONSCIOUS_DURATION) {
                            unconsciousDetected = true;
                            // 여기서 unconsciousDetected를 1로 표현하려면 별도의 int 변수 사용 가능
                            // int unconsciousStatus = 1;
                        }
                    }
                } else {
                    // 10.5 이하 유지 못하면 다시 초기화
                    checkingUnconscious = false;
                }
            }

            String accText = String.format("가속도 센서: X = %.2f, Y = %.2f, Z = %.2f\nroot=%.2f\ncrashDetected=%b\nunconsciousDetected=%b",
                    x, y, z, rootValue, crashDetected, unconsciousDetected);
            tvAccelerometer.setText(accText);
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            String gyroText = String.format("자이로 센서: X = %.2f, Y = %.2f, Z = %.2f", x, y, z);
            tvGyroscope.setText(gyroText);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 정확도 변경 시 필요시 구현
    }

    // 안드로이드 버전 조건문
    private void checkAndroidVersion(){
        // Android 13 이상 권한 확인 및 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 이상
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // 권한 요청
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_POST_NOTIFICATIONS);
            } else {
                // 권한이 이미 허용된 경우
                showNotification();
            }
        } else {
            // Android 8 ~ 12 버전
            showNotification();
        }
    }
    // 알림 표시 메서드
    private void showNotification() {
        PendingIntent mPendingIntent = PendingIntent.getActivity(
                MainActivity.this,
                0,
                new Intent(getApplicationContext(), MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("알림 제목")
                        .setContentText("알림 내용")
                        .setContentIntent(mPendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setAutoCancel(true); // 알림 클릭 시 사라짐

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

    // Notification Channel 생성 (API 26 이상)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mNotificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationChannel.setDescription(CHANNEL_DESCRIPTION);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(mNotificationChannel);
        }
    }

    // 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용된 경우
                showNotification();
            } else {
                // 권한 거부 처리
                System.out.println("알림 권한이 거부되었습니다.");
            }
        }
    }
}
